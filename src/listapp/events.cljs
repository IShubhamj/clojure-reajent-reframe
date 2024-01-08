(ns listapp.events
  (:require
   [re-frame.core :as re-frame]
   [listapp.db :as db]
   [ajax.core :as ajax]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [day8.re-frame.http-fx]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))


(re-frame/reg-event-fx
 ::fetch-users
 (fn [{:keys [db]} _]
   {:db (assoc db :loading true)
    :http-xhrio {:method :get
                 :uri "https://reqres.in/api/users?page=2"
                 :timeout 8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::fetch-user-success]
                 :on-failure [:bad-http-result]}}))

(re-frame/reg-event-db
 ::fetch-user-success
 (fn [db [_ {:keys [data]}]]
   (-> db 
       (assoc :users data)
       (assoc :loading false)
       )
   ))

(re-frame/reg-event-db
 ::update-form
 (fn [db [_ id val]]
   (assoc-in db [:form id] val)))

(re-frame/reg-event-db
 ::save-form
 (fn [db]
   (let [form-data (:form db)
         form-data (assoc form-data :avatar "https://reqres.in/img/faces/1-image.jpg")
         users (get db :users [])
         updated-users (conj users form-data)]
     (assoc db :users updated-users :form {})
     ))
 )