(ns yanatan.statsd-wrapper-test
  (:require [clojure.test :refer :all]
            [clj-statsd :as statsd]
            [yanatan.statsd-wrapper :as stats]
            [clojure.core.async :refer (go <!! >!! >! <! chan close! alt!! timeout)]
            [yanatan.udp :as udp]))

(def -receive (atom nil))

(defn receive []
  (let [c (chan)]
    (go (>! c (@-receive)))
    (go (<! (timeout 100)) (>! c :timeout))
    (<!! c)))

(defn udp-server-fixture [f]
  (let [port (+ 10000 (rand-int 10000))]
    (reset! -receive (udp/make-receive port))
    (stats/setup {:host "127.0.0.1" :port port :prefix "prefix."})
    (f)))

(use-fixtures :once udp-server-fixture)

(deftest raw-statsd-test
  (statsd/increment "abc")
  (await)
  (is (= "abc:1|c" (receive))))

(deftest inc-test
  (testing "send inc"
    (is (some? (stats/incr "key"))))
  (testing "receive from udp socket"
    (is (= "prefix.key:1|c" (receive)))))

(deftest inc-test
  (is (some? (stats/incr "key")))
  (await)
  (is (= "prefix.key:1|c" (receive))))

(deftest timer-test
  (let [t (stats/timer "key")]
    (is (fn? t))
    (<!! (timeout 55))
    (t)
    (await)
    (let [r (receive)]
      (is (not= :timeout r))
      (when (string? r)
        (is (some? (re-find #"prefix.key:(\d+)|t" r)))
        (is (< 55 (->> r (re-find #"prefix.key:(\d+)|t") second Integer/parseInt)))))))

(deftest time-chan-test
  (let [c (chan)
        tc (stats/time-chan c "key")]
    (is (instance? clojure.core.async.impl.channels.ManyToManyChannel tc))
    (<!! (timeout 67))
    (>!! c :object)
    (await)
    (let [r (receive)]
      (is (not= :timeout r))
      (when (string? r)
        (is (some? (re-find #"prefix.key:(\d+)|t" r)))
        (is (< 67 (->> r (re-find #"prefix.key:(\d+)|t") second Integer/parseInt)))))
    (is (= (<!! tc) :object))
    (>!! c :object2)
    (is (= (<!! tc) :object2))))