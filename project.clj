(defproject compressor "0.1"
  :description "Image Compressor"
  :url "https://github.com/scourii/compressor"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.cli "1.0.206"]]
  :repl-options {:init-ns compressor.core}
  :target-path "target/%s" 
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :aliases {}
  :main compressor.core
  :aot [compressor.core compressor.compress])
