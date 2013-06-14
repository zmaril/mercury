(ns Mercury
  (:require [pallet.api :refer [node-spec group-spec server-spec plan-fn]]
            [pallet.actions :refer [package package-manager exec-script*]]
            [pallet.crate.automated-admin-user :refer [automated-admin-user]]
            [pallet.core.data-api :as da]
            [clojure.string :as str])
  (:import [com.tinkerpop.rexster.client RexsterClientFactory RexsterClient])
  (:gen-class
   :methods [^:static [createCluster [] void]
             ^:static [launchCluster [int] void]
             ^:static [connectToCluster [] com.tinkerpop.rexster.client.RexsterClient]
             ^:static [destroyCluster [] void]]
   :main false))
;;TODO: 
;;Open ports for Titan. not sure we can do this progammatically.
;;Use rexster client to return a cluster.
;;Make a method to connect to cluster. 


(def titan-all "titan-all-0.3.1")
(def titan-file (str titan-all ".zip"))
(def titan-url "http://s3.thinkaurelius.com/downloads/titan/")
(def titan-download (str titan-url titan-file ))

(def titan-name "titan-cluster")
(def sh-start-titan 
  "nohup ./bin/titan.sh config/titan-server-rexster.xml config/titan-server-cassandra.properties &")

(def compute-provider (pallet.configure/compute-service "aws"))
(def base-nodes
  (node-spec
   :image {:os-family :ubuntu :os-version-matches "12.04"}
   :hardware {:min-cores 4}
   :network {:inbound-ports [22 80]}))

(def with-packages
  (server-spec 
   :phases {:configure (plan-fn (package-manager :update)
                                ;;                                (package "openjdk-6-jdk")
                                (package "default-jre")
                                (package "wget")
                                (package "unzip"))}))

(def with-titan-up
  (server-spec 
   :phases {:configure (plan-fn
                        (exec-script* (str "wget " titan-download))
                        (exec-script* (str "unzip " titan-file))
                        (exec-script* (str "cd " titan-all "; " sh-start-titan)))}))

(def titan-cluster
  (group-spec titan-name
              :node-spec base-nodes
              :extends [;;(pallet.crate.java/server-spec {})  
                        with-packages with-titan-up]
              :phases {:bootstrap automated-admin-user}))


(defn connect-to-cluster []
  (println "Connecting to cluster...")
  (let [addresses (->> (pallet.api/group-nodes compute-provider 
                                               [titan-cluster])
                   (map #(.getPublicAddresses (:node %)))
                   (apply concat)
                   (str/join ","))]
    (RexsterClientFactory/open addresses) ;;this'll break for more then one  address. Need a custom conf. 

    ))

(defn new-cluster [number-of-servers]
  (println (str "Launching " number-of-servers " servers..."))
  (pallet.api/converge 
   (assoc titan-cluster :count number-of-servers)
   :compute compute-provider))

(defn launch-cluster [number-of-servers]
  (new-cluster number-of-servers)
  (connect-to-cluster))

(defn destroy-cluster []
  (println "Stopping cluster")
  (pallet.api/converge
   (assoc titan-cluster :count 0)
   :compute (pallet.configure/compute-service "aws")))

(defn -newCluster [number-of-servers]
  (launch-cluster number-of-servers))

(defn -connectToCluster []
  (connect-to-cluster))

(defn -launchCluster [number-of-servers]
  (launch-cluster number-of-servers))

(defn -destroyCluster []
  (destroy-cluster))

