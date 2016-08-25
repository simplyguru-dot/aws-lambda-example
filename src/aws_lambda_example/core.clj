(ns aws-lambda-example.core
    (:gen-class :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler])
  (:require [amazonica.aws.ec2 :as ec2]
            [amazonica.aws.elasticloadbalancing :as elb]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.walk :as walk]
            [org.httpkit.client :as http]))

(defn get-health-status [instance]
  {:instance-id (:instance-id instance)
   :state (:state instance)})

(defn get-elb-instances-status [elb-name]
  (->>
   (elb/describe-instance-health :load-balancer-name elb-name)
   :instance-states
   (map get-health-status )))

(defn unhealthy-elb-instances [instances-status]
  (->>
   instances-status
   (remove #(= (:state %) "InService"))
   (map :instance-id)))

(defn handle-event [event]
  (let [instances (get-elb-instances-status
                   (:load-balancer-name
                    (edn/read-string (slurp (io/resource "config.edn")))))
        unhealthy (unhealthy-elb-instances instances)]
    (when (seq unhealthy)
      (pprint "The next instances are unhealthy: ")
      (pprint unhealthy)
      (ec2/terminate-instances :instance-ids unhealthy))
    {:message               (get-in event [:Records 0 :Sns :Message])
     :elb-instance-ids      (mapv :instance-id instances)}))

(defn -handleRequest [this is os context]
  (let [w (io/writer os)]
    (-> (io/reader is)
        json/read
        walk/keywordize-keys
        handle-event
        (json/write w))
    (.flush w)))

