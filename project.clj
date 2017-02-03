(defproject kixi.lapita "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [net.mikera/core.matrix "0.55.0"]
                 [witan.workspace-api "0.1.20"]
                 [prismatic/schema "1.1.3"]
                 [org.clojure/data.csv "0.1.3"]
                 [schema-contrib "0.1.3"]
                 [instaparse "1.4.3"]]
  :main ^:skip-aot kixi.lapita
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[me.raynes/fs "1.4.6"]]}})
