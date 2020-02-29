import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript

export const onNotification=
functions.firestore.document("Notifications").onUpdate(change =>{

 
})
