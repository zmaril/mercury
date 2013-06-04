(ns Mercury
  (:gen-class
   :methods [^:static [launchCluster [int] void]]
   :main false))

(defn launch-cluster [number-of-servers]
  (println (str "About to launch " number-of-servers " servers")))

(defn -launchCluster [number-of-servers]
  (launch-cluster number-of-servers))

