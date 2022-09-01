# SSU-CS-Advising
Android Application | Salem State University Computer Science Department Advising Sign Up.


 The application allows students to sign up for an academic advising appointment in computer science department right from their smartphone devices running Android. It is also providing support for wide range of Android versions. 
In the application an advisee is able to login with his or her school id and email address. Advisee is able to view his or her advisor’s name, room number and a list of available timeslot appointments for booking. Advisee is able to book an appointment, view and cancel any scheduled appointments. 
Email confirmation to advisee and advisor upon booking or canceling of an appointment.
Finally, the developed application provides similar functionality from advisee’s perspective in comparison to the existing web application for academic advising scheduling and took into to an account the revised requirements described further.


<br>



<b>Solution Design</b><br><br>
The application development process included: Creation of the View within the application itself. The View is UI layer and consist of Activities and layout which are shown to a user. Unlike the View, the Model is the data layer and is located within the PHP Rest API on CS Department’s server where most of the business logic take place. The View capture user’s input and forward it to a Presenter. The Presenter’s role is to act as an intermediate between the View and the Model, it listens for the user’s input from the View and update the View appropriately. If the action requires manipulation on the data, the Presenter will communicate directly to the Model by sending http request using post methods where the Model will process the information from the Presenter and retrieve data from the existing database by executing queries. After the response is received by the Model, it will reply back to the Presenter in JSON format so the Presenter can process it accordingly and pass it to the View where it will be displayed it to a user. 


<br>

●	System-Level Architecture Diagram

<p align="center">
<img src="https://user-images.githubusercontent.com/38902416/188000935-0c311a7d-e509-482d-a6fc-88fc4e53100a.png">
</p>
<br>
●	Project Functionality

By using this Android application Advisee can:
		View Advisor name.
		View Advisor room number.
		View booked appointment.
		View list of available appointments.
		Book an appointment.
		Cancel an appointment.
Email confirmation to Advisee and Advisor upon booking or cancellation of an appointment.
<br><br>
●	Usage Diagram

<p align="center"><img src="https://user-images.githubusercontent.com/38902416/188001588-9674e5d6-d2b9-4650-8f5e-792c82b08b1a.png">
</p>
<br>
<br><br>
●	ER-Diagram
<br><br>
<p align="center"><img src="https://user-images.githubusercontent.com/38902416/188001602-3adffe71-67d1-4c7b-af73-25f94734c159.png"></p>
<br><br>
●	Screenshots

First, there is an initial activity which is called Login activity when the application is first launched. Login activity has SSU ID and SSU Email fields for advisee to log in.

<img src="https://user-images.githubusercontent.com/38902416/188001917-acb06260-329f-4756-be7a-62bfce4ea913.png">

<br><br>
After successful authentication process within ‘Login activity, an advisee will be transferred to Main activity where it shows Advisee name, Advisor name, Advisor room number and booked appointment information including date and time of the appointment with option to cancel an appointment if one is booked. Another option provided within the activity is to view available appointments where Advisee will be transferred to next activity, Appointments activity.

<p align="center"><img src="https://user-images.githubusercontent.com/38902416/188001957-cd307726-005c-490f-a8c7-7f21558d6837.png">
<img src="https://user-images.githubusercontent.com/38902416/188001975-d280ff12-cfd8-4c4d-858d-d2b4390cc184.png"></b>

<br><br>
Appointments activity displays list of available appointments for booking with date and time for each appointment. When Advisee selects available appointment Book button will appear. When Book is pressed confirmation dialog pop up and asking Advisee to confirm booking of the appointment. If Yes clicked, another dialog will pop up telling Advisee whether the appointment was booked. After appointment is booked Advisee will be transferred back to Main activity.

<p align="center"><img src="https://user-images.githubusercontent.com/38902416/188002021-f051a712-4883-4d1d-a7a9-bc31175846cb.png">
<img src="https://user-images.githubusercontent.com/38902416/188002041-47907499-cef8-4129-ab4d-95f3ae6cb14f.png"></c>
<br><br><br>

Appointment booking confirmation dialog.
Appointment is scheduled confirmation dialog.


<p align="center">
<img src="https://user-images.githubusercontent.com/38902416/188002073-e515cb17-27f5-4aee-bad9-9b65af3fd577.png">
<img src="https://user-images.githubusercontent.com/38902416/188002089-7fcedcfa-56c9-45b8-b064-00143e8a683a.png">
</p>

<br><br>
Appointment cancellation confirmation dialog.

![image](https://user-images.githubusercontent.com/38902416/188002102-df2bd1cb-8ed0-4bf1-8e6e-3134782eff96.png)




