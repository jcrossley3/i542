(ns i542.core
  (:require [immutant.messaging :as msg]))

(defn -main [& _]
  (msg/queue "/queue/i542", :durable? false))
