(defproject kixi.lapita "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [net.mikera/core.matrix "0.60.3"]
                 [witan.workspace-api "0.1.23"]
                 [prismatic/schema "1.1.6"]
                 [org.clojure/data.csv "0.1.4"]
                 [schema-contrib "0.1.5"]
                 [instaparse "1.4.7"]
                 [thi.ng/geom "0.0.1062"]]
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[me.raynes/fs "1.4.6"]]}})
