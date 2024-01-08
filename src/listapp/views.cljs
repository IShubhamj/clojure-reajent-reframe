(ns listapp.views
  (:require
   [re-frame.core :as re-frame]
   [listapp.subs :as subs]
   [listapp.events :as events]
   ))

(defn display-user [{:keys [id avatar email] first-name :first_name last-name :last_name}]
  [:div.horizontal  {:key id}
   [:img.padding-16 {:src avatar}]
   [:div.padding-16 
    [:h2 first-name " " last-name]
    [:p (str "(" email ")")]
    ]
   ])
(def types ["cat", "dog"])
(defn text-input [id label placeholder]
  (let [value (re-frame/subscribe [::subs/form id])]
    [:div.field
     [:label.label label]
     [:div.control
      [:input.input  {:value @value :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)]) :type "text" :placeholder placeholder}]]])
  )

(defn select-input [id options]
  (let [value (re-frame/subscribe [::subs/form id])]
   [:div.field
   [:label.label "Subject"] 
    [:div.control
     [:div.select
      [:select {:value @value :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])}
       [:option {:value ""} "Please select"]
       (map (fn [o] [:option {:key o :value o} o]) options)
       ]]]
   ]
  ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        loading (re-frame/subscribe [::subs/loading])
        users (re-frame/subscribe [::subs/users])]
    [:div.pannel
     [:div.section
      [text-input :first_name "First Name" "Please enter first name"]
      [text-input :last_name "Last Name" "Please enter last name"] 
      [text-input :email "Email" "Please enter email"]
      [select-input :type types]
      [:button {:on-click #(re-frame/dispatch [::events/save-form])} "Save"]
      ]
     [:div.section
      [:button {:on-click #(re-frame/dispatch [::events/fetch-users])} "Make API Call"]
      (when @loading "Loading...")
      (map display-user @users)]
     [:div
      ]
     ]
    ))
