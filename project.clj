(defproject cljstache "2.0.7-SNAPSHOT"
  :min-lein-version "2.9.8"
  :description "{{ mustache }} for Clojure[Script]"
  :url "http://github.com/fotoetienne/cljstache"
  :license {:name "GNU Lesser General Public License 2.1"
            :url "http://www.gnu.org/licenses/lgpl-2.1.txt"
            :distribution :repo}
  :jvm-opts ["-Xmx1g"]

  :dependencies []

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.11.0"]
                                  [org.clojure/data.json "2.4.0"]]
                   :resource-paths ["test-resources"]
                   :plugins [[jonase/eastwood "1.2.3"]]
                   :eastwood {:linters [:all]
                              :exclude-linters [:boxed-math
                                                :non-clojure-file]
                              :ignored-faults {:unused-ret-vals {cljstache.core {:line 194}}
                                               :unused-fn-args {cljstache.mustache-spec-test true}
                                               :def-in-def {cljstache.mustache-spec-test true}}
                              :debug [:progress :time]}}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :1.10 {:dependencies [[org.clojure/clojure "1.10.3"]]}
             :1.11 {:dependencies [[org.clojure/clojure "1.11.0"]]}
             :cljs {:dependencies [[org.clojure/clojure "1.11.0"]
                                   [org.clojure/clojurescript "1.11.4"]]
                    :plugins [[lein-cljsbuild "1.1.8"]
                              [lein-doo "0.1.11"]]}}

  :aliases {"with-clj" ["with-profile" "dev:dev,1.7:dev,1.8:dev,1.9:dev,1.10:dev,1.11"]
            "with-cljs" ["with-profile" "cljs"]
            "test-clj" ["with-clj" "test"]
            "test-cljs" ["with-cljs" "doo" "nashorn" "test" "once"]
            "test-all" ["do" "clean," "test-clj," "test-cljs"]
            "deploy" ["do" "clean," "deploy" "clojars"]}

  :jar-exclusions [#"\.swp|\.swo|\.DS_Store"]

  :doo {:paths {:rhino "lein run -m org.mozilla.javascript.tools.shell.Main"}}

  :global-vars {*warn-on-reflection* true}

  :clean-targets [:target-path "out"]

  :cljsbuild {:builds
              {:test {:source-paths ["src" "test"]
                      :compiler {:output-to "target/unit-test.js"
                                 :main cljstache.runner
                                 :optimizations :whitespace}}}})
