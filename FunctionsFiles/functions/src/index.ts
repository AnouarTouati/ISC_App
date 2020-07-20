import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
//import { Change } from 'firebase-functions';
//import { Message } from 'firebase-functions/lib/providers/pubsub';

admin.initializeApp()
/*
export const HelloWorld = functions.https.onRequest((request,response)=>{
  response.send("Hello World")
})*/
/*
export const bosttonWeatherUpdate = functions.firestore.document ("cities-weather/boston-ma-us")
  .onUpdate(change=> {
   const dataAfter=change.after.get("temp")
   const dataBefore=change.before.get("temp")
                    const topic = 'Main'
                    const payload2 = { 
                                         notification: {
                                            title: dataAfter,
                                            body: dataAfter
                                                       },
                                        topic : topic
                                     };
                    console.log("My FCM Successfull before:"+dataBefore+"  after:"+dataAfter)
                    
                    return admin.messaging().send(payload2)
                      .catch(error =>{
                        console.log("My FCM Faild",error);
                                      }
                            )
                  }
         )*/
         
export const NewPostCreateNotificationAndSendIt=functions.firestore.document('/AllPosts/{PostID}')
.onCreate(async (snapshot,context)=>{

    const dataNotification={
      name :"null",
      userID : snapshot.get("userID"),
      postID : snapshot.get("postID"),
      notificationText : snapshot.get("cpText"),
      notificationTime : snapshot.get("date"),
      notificationTimeInMillis :snapshot.get("dateInMillis")
    }
    const promise=admin.firestore().collection("Profiles").doc(dataNotification.userID).get()

 const promise2 = promise.then(profileSnapshot=>{
   const name=profileSnapshot.get("name")
  dataNotification.name=name
  return admin.firestore().collection("Notifications").doc(snapshot.get("postID")).set(dataNotification)
 })
       
  const promise3 = promise2.then(writeresult=>{
    const topic="Main"
    const payload={
      notification : {
          title : `${dataNotification.name} Has Posted In ${snapshot.get("checkedDepartments")}`,
          body : dataNotification.notificationText
  
      },
      topic : topic
    }
     return admin.messaging().send(payload)
  })
 
 promise3.catch(error=>{
    console.log("Error Creating Notification")
  })
      
})
