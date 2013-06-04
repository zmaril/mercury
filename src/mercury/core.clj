(ns mercury.core
  (:import (Mercury)))

(defn start-repl []
  (println "Test")
  )

(defn launch []
  (. Mercury launchCluster 0)
)

(defn -main []
  (launch))

