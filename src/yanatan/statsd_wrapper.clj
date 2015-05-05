(ns yanatan.statsd-wrapper
  (:require [clj-statsd :as statsd]
            [clojure.core.async :refer (go-loop chan >! <! close!)]))

(def -prefix (atom ""))

(defn setup [{:keys [host port prefix]}]
  (statsd/setup host port)
  (reset! -prefix prefix))

(defn timer
  "Return a function that fires a timing of the time between calls"
  [key]
  (let [start (System/currentTimeMillis)]
    #(statsd/timing (str @-prefix key) (- (System/currentTimeMillis) start))))

(defn incr
  ([key] (incr key 1))
  ([key n] (statsd/increment (str @-prefix key) n)))

(defn time-chan
  "Fire a timing of the time between this call and the first object on the channel"
  [c key]
  (let [fire (timer key)
        c2 (chan)]
    (go-loop [fired false]
      (if-let [v (<! c)]
        (do (when (not fired) (fire))
            (>! c2 v)
            (recur true))
        (close! c2)))
    c2))