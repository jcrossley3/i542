(ns i542.core-test
  (:require [fntest.core :refer :all]
            [clojure.test :refer :all]
            [immutant.messaging :as msg]))

(def opts {:host "localhost", :remote-type :hornetq-wildfly,
           :username "testuser", :password "testuser"})

(use-fixtures :once
  (compose-fixtures
    (partial with-jboss #{:isolated :offset})
    (with-deployment "i542.war" ".")))

(deftest publish-and-receive
  (with-open [ctx1 (msg/context (assoc opts :port (offset-port :http)))]
    (let [q1 (msg/queue "/queue/i542" :context ctx1)
          c 100]
      (dotimes [i c]
        (msg/publish q1 i))
      (is (= (range c)
            (repeatedly c
              #(let [t1 (System/currentTimeMillis)
                     m  (msg/receive q1 :timeout 10000)
                     t2 (System/currentTimeMillis)]
                 (println (format "Got %s (%s ms)" m (- t2 t1)))
                 m)))))))

(deftest publish-and-listen
  (with-open [ctx1 (msg/context (assoc opts :port (offset-port :http)))]
    (let [q1 (msg/queue "/queue/i542" :context ctx1)
          received (atom [])
          p (promise)
          c 100]
      (dotimes [i c]
        (msg/publish q1 i))
      (msg/listen q1 (fn [m]
                       (println "GOT:" m)
                       (swap! received conj m)
                       (when (= c (count @received))
                         (deliver p :done))))
      (deref p 60000 nil)
      (is (= (range c) @received)))))
