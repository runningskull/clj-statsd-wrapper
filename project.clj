(defproject org.clojars.yanatan16/statsd-wrapper "0.1.0"
  :description "Wrap statsd for convenience"
  :url "https://github.com/yanatan16/clj-statsd-wrapper"
  :license {:name "MIT"
            :url "https://github.com/yanatan16/clj-statsd-wrapper/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-statsd "0.3.10"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.7"]]
                   :plugins []
                   :resource-paths ["test-resources"]}})
