(ns mercury.cloud
  (:require [pallet.api :as api]
            [pallet.actions :as actions]
            [pallet.configure :as conf]
            [pallet.crate.automated-admin-user :refer
             [automated-admin-user]]
            [pallet.crate.titan   :as titan]
            [pallet.crate.java    :as java]
            [pallet.crate.upstart :as upstart]
            [clojure.string :as str])
  (:import [com.tinkerpop.rexster.client 
            RexsterClientFactory RexsterClient]))

(def compute-provider (pallet.configure/compute-service "aws"))

(def base-nodes 
  (api/node-spec
   :image {:os-family :ubuntu :os-version-matches "12.04"}
   :hardware {:min-cores 4}
   :network {:inbound-ports [22 ;;SSH
                             80 ;;HTTP?
                             7000 ;;Cassandra cluster communication
                             7001 ;;Cassandra SSL 
                             7199 ;;JMX                             
                             9160 ;;Thift                             
                             8182 ;;Rexster                             
                             ]}))

(def titan-cluster
  (api/group-spec "titan-cluster"
                  :node-spec base-nodes
                  :default-phases [:bootstrap :install :configure :run]
                  :extends [(java/server-spec {})
                            (upstart/server-spec {})
                            (titan/server-spec {:supervisor :upstart})]
                  :phases {:bootstrap automated-admin-user
                           :install   (api/plan-fn 
                                       (actions/package-manager :update))}))


(defn connect-to-cluster []
  (println "Connecting to cluster...")
  (let [addresses (->> (api/group-nodes compute-provider 
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







