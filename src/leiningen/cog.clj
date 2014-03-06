(ns leiningen.cog
  (:gen-class)
  (:use [clojure.java.io :only [file]])
  (:require leiningen.help
            leiningen.ring.server
            clojure.java.shell
            [watchtower.core :as watchtower]
            robert.hooke))


(defn all-sub-dirs
  [dirs]
  (let [dirs (map clojure.java.io/file dirs)
        all-sub-paths (flatten (map file-seq dirs))
        all-dirs (filter #(.isDirectory %) all-sub-paths)
        result (map #(.getCanonicalPath %) all-dirs)]
    result))

(defn build-target
  [project target-name target]
  (println "building" target-name)
  (let [{exec :exec} target
        {root :root} project]
    (let [{exit :exit
           out :out
           err :err} (apply clojure.java.shell/sh (concat exec [:dir root]))]
      (when (not (= exit 0))
        (println "Unexpected error building: " target-name exit)
        (println err)))))

(defn build
  "Build all targets"
  [{{targets :targets} :cog :as project}]
  (doseq [[k v] targets]
    (build-target project k v)))

(defn watch-target
  [project target-name {watch-dirs :watch :as target}]
  (println "watching" target-name watch-dirs)
  (watchtower/watcher watch-dirs
                      (watchtower/rate 1000)
                      (watchtower/on-change (fn [& _] (Thread. #(build-target project target-name target))))))

(defn watch
  "Watch all targets"
  [project]
  (println "watching project for changes")
  (let [{{targets :targets} :cog} project]
    (doseq [[tname t] targets]
      (watch-target project tname t))
    (loop []
      (Thread/sleep 1000)
      (recur))))

(defn cog
  {:subtasks [#'build
              #'watch]}
  [project & [cmd & args]]
  (cond
    (= cmd "watch") (apply watch project args)
    (= cmd "build") (apply build project args)
    :else (println (leiningen.help/help-for project "cog"))))

(defn watcher-task
  [f project & args]
  (doto
    (Thread. #(watch project))
    (.start))
  (apply f project args))

