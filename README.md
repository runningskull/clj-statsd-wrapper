# clj-statsd-wrapper

A Clojure library that wraps `clj-statsd` and provides some convenient methods.

```
[org.clojars.yanatan16/statsd-wrapper "0.1.0"]
```

## Usage

```
(require '[yanatan.statsd-wrapper :as stats]
         '[clojure.core.async :refer (<!! timeout)])

(stats/setup {:host "localhost" :port 8125 :prefix "myprefix."})

(stats/incr "key") ; implied 1
(stats/incr "key" 2)

(let [t (stats/timer "key")]
    (<!! (timeout 1000))
    (t))
;; myprefix.key:1000|t

(let [c (chan)
      tc (stats/time-chan c "key")]
    (<!! (timeout 1000))
    (>!! c :object)
    (is = :object (<!! tc))) ;; c is consumed, so use tc which will forward all items
;; sends timer on first object on channel
;; myprefix.key:1000|t
```

## License

MIT. See `LICENSE` file.