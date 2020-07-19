import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
//import { Change } from 'firebase-functions';
//import { Message } from 'firebase-functions/lib/providers/pubsub';

admin.initializeApp()
/*
export const HelloWorld = functions.https.onRequest((request,response)=>{
  response.send("Hello World")
})*/

export const bosttonWeatherUpdate = functions.firestore.document ("cities-weather/boston-ma-us")
  .onUpdate(change=> {
   const datemodified=change.after.updateTime;
                    const topic = 'Main'
                    const payload2 = { 
                                         notification: {
                                            title: 'Anouar has posted in Media '+datemodified?.toDate.toString(),
                                            body: 'Event X postponed'
                                                       },
                                        topic : topic
                                     };
                    console.log("My FCM Successfull "+datemodified?.toDate.toString())
                    
                    return admin.messaging().send(payload2)
                      .catch(error =>{
                        console.log("My FCM Faild",error);
                                      }
                            )
                  }
         )
         /*
export const NewPostCreateNotificationAndSendIt=functions.firestore.document('/AllPosts/{PostID}')
.onCreate(async (snapshot,context)=>{
  try{
    const postData=snapshot.data()

    const dataNotification={
      userID : postData.userID,
      notificationText : `${postData.} has posted in the department of ${postData.checkedDepartments}`,
      notificationTime : postData.notificationTime,
      notificationTimeInMillis :postData.notificationTimeInMillis
    }
  await admin.firestore().collection("Notifications").doc(postData.postID).set(dataNotification)

  const topic="Main"
  const payload={
    notification : {
        title : `SomeOne Has Posted In ${notificationData.checkedDepartments}`,
        body : "Event X postponed"

    },
    topic : topic
  }
  return admin.messaging().send(payload)
  }catch(err){
console.log("Error Creating Notification")
  }
    
})*/
