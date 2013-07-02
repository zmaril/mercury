(ns Mercury
  (:require [mercury.cloud :as cloud])
  (:import [com.tinkerpop.rexster.client 
            RexsterClientFactory RexsterClient])
  (:gen-class
   :methods [^:static [createCluster [] void]
             ^:static [launchCluster [int] 
                       com.tinkerpop.rexster.client.RexsterClient]
             ^:static [connectToCluster [] 
                       com.tinkerpop.rexster.client.RexsterClient]
             ^:static [destroyCluster [] void]]
  :main false))

(defn -newCluster [number-of-servers]
  (cloud/launch-cluster number-of-servers))

(defn -connectToCluster []
  (cloud/connect-to-cluster))

(defn -launchCluster [number-of-servers]
  (cloud/launch-cluster number-of-servers))

(defn -destroyCluster []
  (cloud/destroy-cluster))

