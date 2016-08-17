(ns aws-lambda-example.core
  (:gen-class :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:require [amazonica.aws.ec2 :as ec2]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.walk :as walk]
            [org.httpkit.client :as http]))


(defn get-instance-id-and-private-ips [instance]
  {:instance-id (:instance-id instance)
   :private-ip (->> instance
                     :network-interfaces
                     (map :private-ip-address)
                     clojure.string/join)})

(defn get-instances-ec2 []
  (->> (ec2/describe-instances
        :filters [{:name "tag:env"
              :values [(:env (edn/read-string (slurp (io/resource "config.edn"))))]}
             {:name   "tag:type"
              :values [(:type (edn/read-string (slurp (io/resource "config.edn"))))]}
             {:name   "instance-state-name"
              :values ["running" "pending"]}])
       :reservations
       (mapcat :instances)
       (map get-instance-id-and-private-ips)))


(defn handle-event [event]
  "Main handler of an AWS Event"
  (let [instances (get-instances-ec2)]
    (pprint "Hello I'm Lambda function")
    (pprint instances)
    {:message           (get-in event [:Records 0 :Sns :Message])
     :instance-ids      (mapv :instance-id instances)}))


(defn -handleRequest [this is os context]
  "Parser of input and genarator of JSON output"
  (let [w (io/writer os)]
    (-> (io/reader is)
        json/read
    (-> (io/reader is)
        json/read
        walk/keywordize-keys
        handle-event
        (json/write w))
    (.flush w))))

