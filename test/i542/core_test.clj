(ns i542.core-test
  (:require [fntest.core :refer :all]
            [clojure.test :refer :all]
            [immutant.messaging :as msg])
  (:import java.util.Date))

(def opts {:host "localhost", :remote-type :hornetq-wildfly,
           :username "testuser", :password "testuser"})

(use-fixtures :once
  (compose-fixtures
    (partial with-jboss #{:isolated :offset})
    (with-deployment "i542.war" ".")))

(deftest publish-and-receive
  (with-open [ctx1 (msg/context (assoc opts :port (offset-port :http)))]
    (let [q1 (msg/queue "/queue/i542" :context ctx1)
          c 1000]
      (dotimes [i c]
        (msg/publish q1 i))
      (is (= (range c)
            (repeatedly c
              (comp (fn [m] (println (str (Date.)) "GOT:" m) m)
                (partial msg/receive q1))))))))
