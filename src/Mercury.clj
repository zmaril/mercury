(ns Mercury
  (:require [pallet.api :refer [node-spec group-spec server-spec]]
            [pallet.crate.cassandra :as cassandra]
            [pallet.crate.automated-admin-user :refer [automated-admin-user]])
  (:gen-class
   :methods [^:static [launchCluster [int] void]
             ^:static [destroyCluster [] void]]
   :main false))

(def base-nodes
  (node-spec
   :image {:os-family :ubuntu :os-version-matches "10.10"}
   :hardware {:min-cores 1}
   :network {:inbound-ports [22 80]
;;             :security-group "titan-group"
             }))

(def titan-cluster
  (group-spec "titan-cluster"
;;              :extends [(cassandra/server-spec {})] 
              :node-spec base-nodes
              :phases {:bootstrap automated-admin-user}))


(defn launch-cluster [number-of-servers]
  (println (str "About to launch " number-of-servers " servers"))
  (pallet.api/converge 
   (assoc titan-cluster :count number-of-servers)
   :compute (pallet.configure/compute-service "aws")))


(defn destroy-cluster []
  (println "Stopping cluster")
  (pallet.api/converge
   (assoc titan-cluster :count 0)
   :compute (pallet.configure/compute-service "aws")))

(defn -launchCluster [number-of-servers]
  (launch-cluster number-of-servers))

(defn -destroyCluster []
  (destroy-cluster))

