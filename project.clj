(defproject mercury "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[com.palletops/pallet-lein "0.6.0-beta.9"]]
  :dependencies [[org.clojure/clojure "1.5.1"]

                 ;;Groovy shell
                 [com.tinkerpop.gremlin/gremlin-groovy "2.3.0"]

                 ;;Rexster 
                 [com.tinkerpop.rexster/rexster-protocol "2.3.0"]

                 ;;Pallet 
                 [com.palletops/pallet "0.8.0-beta.10"]
                 [ch.qos.logback/logback-classic "1.0.1"]
                 [org.cloudhoist/pallet-jclouds "1.5.2"]
                 [org.jclouds/jclouds-allblobstore "1.5.5"]
                 [org.jclouds/jclouds-allcompute "1.5.5"]
                 [org.jclouds.driver/jclouds-slf4j "1.5.5"
                  :exclusions [org.slf4j/slf4j-api]]
                 [org.jclouds.driver/jclouds-sshj "1.5.5"]

                 ;;Crates
                 [com.palletops/cassandra-crate "0.8.0-alpha.1"]
                 [com.palletops/java-crate "0.8.0-beta.5"]]
  :repositories
  {"sonatype" "https://oss.sonatype.org/content/repositories/releases/"}
  :aot [Mercury]
  :main mercury.core)
