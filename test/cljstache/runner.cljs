(ns cljstache.runner
  "A stub namespace to run cljs tests using doo"
  (:require [cljstache.core-test]
            [cljstache.mustache-spec-test]
            [doo.runner :refer-macros [doo-tests]]))

(doo-tests 'cljstache.core-test
           'cljstache.mustache-spec-test)
