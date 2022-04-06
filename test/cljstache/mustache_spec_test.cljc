(ns cljstache.mustache-spec-test
  "Test against the [Mustache spec](http://github.com/mustache/spec)"
  (:require [cljstache.core :refer [render]]
            [clojure.string :as str]
            #?(:clj [clojure.data.json :as json])
            #?(:clj [clojure.edn :as edn])
            #?(:clj [clojure.java.io :as io])
            #?(:clj [clojure.test :refer [deftest is]])
            #?(:cljs [cljs.test :refer-macros [deftest is]]))
  #?(:cljs (:require-macros [cljstache.mustache-spec-test :refer [load-specs]])))

;; We load the specs at compile time via macro
;; for clojurescript compatibility

(def specs ["comments" "delimiters" "interpolation" "sections" "inverted" "partials" "~lambdas"])

#?(:clj (defn- spec-path [spec] (str "test-resources/spec/specs/" spec ".json")))

#?(:clj (defn- load-spec-tests [spec]
          (-> spec spec-path slurp json/read-json :tests)))

#?(:clj (defn- read-lambda [^String lambda-str]
          (let [lambda-str-reader (io/reader (.getBytes lambda-str "UTF-8"))
                in (java.io.PushbackReader. lambda-str-reader)]
            (loop [forms []]
              (let [form (edn/read {:eof nil} in)]
                (if (nil? form)
                  (if (> (count forms) 1)
                    (reverse (into '(do) forms))
                    (first forms))
                  (recur (conj forms form))))))))

#?(:clj (defn- build-lambda [test]
          (update-in test [:data :lambda :clojure]
                     (fn [lambda-str]
                       {:readable-lambda lambda-str
                        :lambda-fn (read-lambda lambda-str)}))))

#?(:clj (defmacro load-specs []
          (into {} (for [spec specs]
                     (let [tests (load-spec-tests spec)]
                       (if (= spec "~lambdas")
                         [spec (mapv build-lambda tests)]
                         [spec tests]))))))

(def spec-tests (load-specs))

(defn- update-lambda-in [data f]
  (if (contains? data :lambda)
    (update data :lambda f)
    data))

(defn- extract-lambdas [data]
  (update-lambda-in data #(-> % :clojure :readable-lambda)))

(defn- load-lambdas [data]
  (update-lambda-in data #(-> % :clojure :lambda-fn)))

(defn- flatten-string [^String s]
  (str/replace (str/replace s "\n" "\\\\n") "\r" "\\\\r"))

(defn run-spec-test [spec-test]
  (let [template (:template spec-test)
        readable-data (extract-lambdas (:data spec-test))
        data (load-lambdas (:data spec-test))
        partials (:partials spec-test)]
    (is (= (:expected spec-test)
           (render template data partials))
        (str (:name spec-test) " - " (:desc spec-test) "\nTemplate: \""
             (flatten-string template) "\"\nData: " readable-data
             (when partials (str "\nPartials: " partials))))))

(defn run-spec-tests [spec]
  (doseq [spec-test (spec-tests spec)]
    (run-spec-test spec-test)))

(deftest test-comments
  (run-spec-tests "comments"))

(deftest test-delimiters
  (run-spec-tests "delimiters"))

(deftest test-interpolation
  (run-spec-tests "interpolation"))

(deftest test-sections
  (run-spec-tests "sections"))

(deftest test-inverted
  (run-spec-tests "inverted"))

(deftest test-partials
  (run-spec-tests "partials"))

(deftest test-lambdas
  (run-spec-tests "~lambdas"))
