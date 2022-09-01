<?php

 include 'config.php';


 $task = "";
 $task = $_POST['task'];
	 
	
	// Load information for MainActivity	
    if(isset($_POST['id']) && isset($_POST['email']) AND ($task == "MainPageInfo"))
    {
		// Innitialize Variables
		$result=array();
		$id = $_POST['id'];
        $email = $_POST['email'];
		  
		// Query database for Advisee_ID, Advisor_ID, Advisee_Name, Aptmtmade
        $sql = 'SELECT Advisee_ID, Advisor_ID, Advisee_Name, Aptmtmade FROM advisee WHERE  Advisee_ID = :id AND Advisee_Email = :email';
        $stmt = $conn->prepare($sql);
        $stmt->bindParam(':id', $id, PDO::PARAM_STR);
        $stmt->bindParam(':email', $email, PDO::PARAM_STR);
        $stmt->execute();
		
		// If row exist load information
		if($stmt->rowCount())
        {
			$row_all = $stmt->fetchall(PDO::FETCH_ASSOC);

			// Assign result variables to result array
			$result[0]['Advisee_ID'] = $row_all[0]['Advisee_ID'];
			$result[0]['Advisor_ID'] = $row_all[0]['Advisor_ID'];
			$result[0]['Advisee_Name'] = $row_all[0]['Advisee_Name'];
			$result[0]['Aptmtmade'] = $row_all[0]['Aptmtmade'];

			$Advisor_ID = $row_all[0]['Advisor_ID'];
		
			// Query database for Advisor_Name, Room_Num, Advisor_Email
			$sql1 = 'SELECT Advisor_Name, Room_Num, Advisor_Email FROM advisor WHERE Advisor_ID = :Advisor_ID';
			$stmt = $conn->prepare($sql1);
			$stmt->bindParam(':Advisor_ID', $Advisor_ID, PDO::PARAM_STR);
			$stmt->execute();
			
			// If row exist load information
			if($stmt->rowCount())
			{
				$row_all1 = $stmt->fetchall(PDO::FETCH_ASSOC);
					
				// Assign result variables to result array	
				$result[0]['Advisor_Name'] = $row_all1[0]['Advisor_Name'];
				$result[0]['Room_Num'] = $row_all1[0]['Room_Num'];
				$result[0]['Advisor_Email'] = $row_all1[0]['Advisor_Email'];
					

				$Advisee_ID = $row_all[0]['Advisee_ID'];
				$Avaiablity = "0";
				
				// Query database for Appointment_ID, Aptmt_Date, Aptmt_Time, show_not
				$sql2 = 'SELECT Appointment_ID, Aptmt_Date, Aptmt_Time, show_not FROM appointment WHERE Advisee_ID = :Advisee_ID AND Avaiablity = :Avaiablity';
				$stmt = $conn->prepare($sql2);
				$stmt->bindParam(':Advisee_ID', $id, PDO::PARAM_STR);
				$stmt->bindParam(':Avaiablity', $Avaiablity, PDO::PARAM_STR);
				$stmt->execute();
				
				// If row exist load information
				if($stmt->rowCount())
				{	
					$row_all2 = $stmt->fetchall(PDO::FETCH_ASSOC);
				
					// Assign result variables to result array
					$result[0]['Appointment_ID'] = $row_all2[0]['Appointment_ID'];
					$result[0]['Aptmt_Time'] = $row_all2[0]['Aptmt_Time'];
					$result[0]['Aptmt_Date'] = $row_all2[0]['Aptmt_Date'];
					$result[0]['show_not'] = $row_all2[0]['show_not'];
					
					// Send result in JSON	
					header('Content-type: application/json');
					echo json_encode($result);
				}
				
				elseif(!$stmt->rowCount())
				{

					// Send result in JSON	
					header('Content-type: application/json');
					echo json_encode($result);
				}
			} 
			
			elseif(!$stmt->rowCount())
			{
				// Send result in JSON
				header('Content-type: application/json');
				echo json_encode($result);
			}
		
		}
		
		elseif(!$stmt->rowCount())
		{
			// Send result in JSON
			header('Content-type: application/json');
			echo json_encode($result);
		}
	}
	 
	 
	 
	 

	 
	// Load Available Appointments	
    if(isset($_POST['id']) && isset($_POST['email']) AND ($task == "showAvailableAppointments"))
    {
		// Innitialize Variable
		$result=array();
	   	$id = $_POST['id'];
        $email = $_POST['email'];
		
		// Query database for Advisor_ID, Advisee_Name
        $sql = 'SELECT Advisor_ID, Advisee_Name FROM advisee WHERE  Advisee_ID = :id AND Advisee_Email = :email';
        $stmt = $conn->prepare($sql);
        $stmt->bindParam(':id', $id, PDO::PARAM_STR);
        $stmt->bindParam(':email', $email, PDO::PARAM_STR);
        $stmt->execute();
		  
		// If row exist load Available Appointments
		if($stmt->rowCount())
        {
			$row_all = $stmt->fetchall(PDO::FETCH_ASSOC);
			header('Content-type: application/json');

			// Assign result variables to result array
			$Advisor_ID = $row_all[0]['Advisor_ID'];
			$Avaiablity = "1";
			
			// Query database for Appointment_ID, Aptmt_Date, Aptmt_Time
			$sql1 = "SELECT Appointment_ID, Aptmt_Date, Aptmt_Time FROM appointment WHERE  Advisor_ID = :Advisor_ID AND Avaiablity = :Avaiablity AND CONCAT(Aptmt_Date,' ',Aptmt_Time) >= DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i') ORDER BY Aptmt_Date, Aptmt_Time";
			$stmt = $conn->prepare($sql1);
			$stmt->bindParam(':Advisor_ID', $Advisor_ID, PDO::PARAM_STR);
			$stmt->bindParam(':Avaiablity', $Avaiablity, PDO::PARAM_STR);
			$stmt->execute();
			
			// If there are Appointment, Load Appointments
			if($stmt->rowCount())
			{
				$result = $stmt->fetchall(PDO::FETCH_ASSOC);
				
				// Send result in JSON
				header('Content-type: application/json');
				echo json_encode($result);
		
			} 
			
			// If there are no Appointments
			elseif(!$stmt->rowCount())
			{
				echo "noAppointments";
			}	
		}
		
		// Error, no Advisor
		elseif(!$stmt->rowCount())
		{
			echo "Error!";
        }
	}
	 
	
	
	
	 	 
	// Book Appointment	
	if(isset($_POST['id']) && isset($_POST['appointmentId']) AND ($task == "bookAptmt"))
	{
		// Innitialize Variable
		$result="";
	   	$id = ($_POST['id']);
        $appointmentId = $_POST['appointmentId'];
		$Avaiablity = "0";
		$nAvaiablity = "1";
		$aptmtmade = "1"; 
		  
		// Check if Advisee already booked an Appointment
        $sql = 'SELECT * FROM appointment WHERE Advisee_ID = :Advisee_ID AND Avaiablity = :nAvaiablity';
        $stmt = $conn->prepare($sql);
        $stmt->bindParam(':Advisee_ID', $id, PDO::PARAM_STR);
	    $stmt->bindParam(':nAvaiablity', $Avaiablity, PDO::PARAM_STR);
        $stmt->execute();
		 
		// Appointment is already booked
		if($stmt->rowCount())
		{
			$result = "alreadyBooked";
		}
		
		// No Appointment booked, continue booking
		elseif($stmt->rowCount()==0)
		{
			// Query to Book Appointment if it is still Available and not old
			$sql1 = "UPDATE appointment SET Avaiablity = :Avaiablity, Advisee_ID = :Advisee_ID WHERE Avaiablity = :nAvaiablity AND Appointment_ID = :Appointment_ID AND CONCAT(Aptmt_Date,' ',Aptmt_Time) >= DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i');";
		
			// Check if Appointment is Booked
			$sql1.= "SELECT Appointment_ID FROM appointment WHERE Advisee_ID = :Advisee_ID AND Avaiablity = :Avaiablity AND CONCAT(Aptmt_Date,' ',Aptmt_Time) >= DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i');";
			$stmt = $conn->prepare($sql1);
			$stmt->bindParam(':Advisee_ID', $id, PDO::PARAM_STR);
			$stmt->bindParam(':Avaiablity', $Avaiablity, PDO::PARAM_STR);
			$stmt->bindParam(':Appointment_ID', $appointmentId, PDO::PARAM_STR);
			$stmt->bindParam(':nAvaiablity', $nAvaiablity, PDO::PARAM_STR);
			$stmt->execute();
			
		 
			// If Booked
			if($stmt->rowCount())
			{
				// Query to flag that Advisee booked Appointment
				$sql111 = "UPDATE advisee SET Aptmtmade = :Aptmtmade WHERE Advisee_ID = :Advisee_ID;";
				$stmt = $conn->prepare($sql111);
				$stmt->bindParam(':Aptmtmade', $aptmtmade, PDO::PARAM_STR);
				$stmt->bindParam(':Advisee_ID', $id, PDO::PARAM_STR);
				$stmt->execute();
				
				// Appointment Booked set result true
				$result = "true";	
	
				
				/// Email Confirmation ///////////////
				
				// Innitialize Variable
				$result1=array();
				
				// Query for Advisor_ID, Advisee_Email, Advisee_Name
				$sql1 = 'SELECT Advisor_ID, Advisee_Email, Advisee_Name from advisee WHERE Advisee_ID = :Advisee_ID';
				$stmt = $conn->prepare($sql1);
				$stmt->bindParam(':Advisee_ID', $id, PDO::PARAM_STR);
				$stmt->execute();
				
				$row_all = $stmt->fetchall(PDO::FETCH_ASSOC);

				// Assign result variables to result array
				$result1[0]['Advisee_Email'] = $row_all[0]['Advisee_Email'];
				$result1[0]['Advisee_Name'] = $row_all[0]['Advisee_Name'];
				$result1[0]['Advisor_ID'] = $row_all[0]['Advisor_ID'];

				$Advisor_ID = $row_all[0]['Advisor_ID'];
		
				// Query for Advisor_Name, Advisor_Email
				$sql1 = 'SELECT Advisor_Name, Advisor_Email FROM advisor WHERE Advisor_ID = :Advisor_ID';
				$stmt = $conn->prepare($sql1);
				$stmt->bindParam(':Advisor_ID', $Advisor_ID, PDO::PARAM_STR);
				$stmt->execute();
			
				// If Advisor exist load its info
				if($stmt->rowCount())
				{
					$row_all1 = $stmt->fetchall(PDO::FETCH_ASSOC);
					
					// Assign result variables to result array
					$result1[0]['Advisor_Name'] = $row_all1[0]['Advisor_Name'];
					$result1[0]['Advisor_Email'] = $row_all1[0]['Advisor_Email'];
					
					$Advisee_ID = $result1[0]['Advisor_ID'];
					
					// Query for Aptmt_Date, Aptmt_Time
					$sql2 = 'SELECT Aptmt_Date, Aptmt_Time FROM appointment WHERE Advisee_ID = :Advisee_ID';
					$stmt = $conn->prepare($sql2);
					$stmt->bindParam(':Advisee_ID', $id, PDO::PARAM_STR);
					$stmt->execute();
				
					// If Appointment exist, load its info
					if($stmt->rowCount())
					{	
						$row_all2 = $stmt->fetchall(PDO::FETCH_ASSOC);
				
						// Assign result variables to result array
						$result1[0]['Aptmt_Time'] = $row_all2[0]['Aptmt_Time'];
						$result1[0]['Aptmt_Date'] = $row_all2[0]['Aptmt_Date'];
					
						// Innitialize Variables
						$Advisee_Email = $result1[0]['Advisee_Email'];
						$Advisee_Name = $result1[0]['Advisee_Name'];
						$Advisor_Name = $result1[0]['Advisor_Name'];
						$Advisor_Email = $result1[0]['Advisor_Email'];
						$Appointment_Time = $result1[0]['Aptmt_Time'];
						$Appointment_Date = $result1[0]['Aptmt_Date'];
						
						// Mail headers
						$headers = 'From: noreply@salemstate.edu' . "\r\n";
						$headers .= 'MIME-Version: 1.0' . "\r\n";
						$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";
		
						// Mail title
						$msg = "Appointment Booked!";
						
						// Mail body Advisor
						$bodyAdvisor = "Dear " . $Advisor_Name . ",<br>";
						$bodyAdvisor .= "<p>Advising Appointment Booked!</p><br>";
                		$bodyAdvisor .= "<p><u>Appointment Details</u></p>";
                		$bodyAdvisor .= "<p>Student Name: " . $Advisee_Name . "<p>";
                		$bodyAdvisor .= "<p>Student ID: " . $id . "<p>";
                		$bodyAdvisor .= "<p>Appointment Date: " . $Appointment_Date . "<p>";
                		$bodyAdvisor .= "<p>Appointment Time: " . $Appointment_Time . "<p>";
                		$bodyAdvisor .= "<p><i>DO NOT REPLY TO THIS Email.<i><p>";
				
						// Mail body Advisee
						$bodyAdvisee = "Dear " . $Advisee_Name . ",<br>";
						$bodyAdvisee .= "<p>You Booked Advising Appointment</p>";
						$bodyAdvisee .= "<p>Your Student ID: " . $id . "<p>";
						$bodyAdvisee .= "<p>Advisor Name: " . $Advisor_Name . "<p><br>";
						$bodyAdvisee .= "<p><u>Appointment Details</u></p>";
						$bodyAdvisee .= "<p>Appointment Date: " . $Appointment_Date . "<p>";
						$bodyAdvisee .= "<p>Appointment Time: " . $Appointment_Time . "<p>";
						$bodyAdvisee .= "<p><i>DO NOT REPLY TO THIS Email.<i><p>";

						// Send email confirmation to Advisee and Advisor
						mail($Advisor_Email, $msg, $bodyAdvisor, $headers);
						mail($Advisee_Email, $msg, $bodyAdvisee, $headers);  
								
					}  
					
					// Appointment does not exist
					elseif(!$stmt->rowCount())
					{

					}
				}
				
				// Advisor does not exist
				elseif(!$stmt->rowCount())
				{
					
				}	
			}
			
			// Appointment is not booked
			elseif(!$stmt->rowCount())
			{
				$result = "false";
			}	
		}
		
		else
		{
			$result = "error";
		}
		
		// Send reply back
		echo $result;
	}
	 
	 
	 
	 
	 
	 
	 
	// Cancell Appointment
	if(isset($_POST['id']) && isset($_POST['appointmentId']) AND ($task == "cancel"))
	{
		// Innitialize Variable
		$result="";
	   	$id = ($_POST['id']);
        $appointmentId = $_POST['appointmentId'];
		$Avaiablity = "0";
		$nAvaiablity = "1";
		$aptmtmade = "0"; 
		$nAdvisee_ID = NULL;
		  

		// Query to Cancell Appointment and check if it is available
        $sql1 = "UPDATE appointment SET Advisee_ID = :nAdvisee_ID WHERE Appointment_ID = :Appointment_ID;";
		$sql1.= "UPDATE appointment SET Avaiablity = :Avaiablity WHERE Appointment_ID = :Appointment_ID;";
		$sql1.= "SELECT Appointment_ID FROM appointment WHERE Appointment_ID = :Appointment_ID AND Avaiablity = :Avaiablity;";
        $stmt = $conn->prepare($sql1);
		$stmt->bindParam(':nAdvisee_ID', $nAdvisee_ID, PDO::PARAM_STR);
        $stmt->bindParam(':Advisee_ID', $id, PDO::PARAM_STR);
        $stmt->bindParam(':Avaiablity', $nAvaiablity, PDO::PARAM_STR);
		$stmt->bindParam(':Appointment_ID', $appointmentId, PDO::PARAM_STR);
		$stmt->bindParam(':nAvaiablity', $nAvaiablity, PDO::PARAM_STR);
        $stmt->execute();
		
		// If Appointment is Cancelled, set Advisee no appointment made
		if($stmt->rowCount())
		{
			$sql111 = 'UPDATE advisee SET Aptmtmade = :Aptmtmade WHERE Advisee_ID = :Advisee_ID';
			$stmt = $conn->prepare($sql111);
			$stmt->bindParam(':Aptmtmade', $aptmtmade, PDO::PARAM_STR);
			$stmt->bindParam(':Advisee_ID', $id, PDO::PARAM_STR);
			$stmt->execute();
	
			// Appointment Cancelled set result to true	
			$result = "true";
				
			// Email confirmation
					
			// Innitialize Variable
			$result1=array();
				
			// Query for Advisor_ID, Advisee_Email, Advisee_Name	
			$sql1 = 'SELECT Advisor_ID, Advisee_Email, Advisee_Name from advisee WHERE Advisee_ID = :Advisee_ID';
			$stmt = $conn->prepare($sql1);
			$stmt->bindParam(':Advisee_ID', $id, PDO::PARAM_STR);
			$stmt->execute();
				
			$row_all = $stmt->fetchall(PDO::FETCH_ASSOC);

			// Assign result variables to result array
			$result1[0]['Advisee_Email'] = $row_all[0]['Advisee_Email'];
			$result1[0]['Advisee_Name'] = $row_all[0]['Advisee_Name'];
			$result1[0]['Advisor_ID'] = $row_all[0]['Advisor_ID'];

			$Advisor_ID = $row_all[0]['Advisor_ID'];
		
			// Query for Advisor_Name, Advisor_Email
			$sql1 = 'SELECT Advisor_Name, Advisor_Email FROM advisor WHERE Advisor_ID = :Advisor_ID';
			$stmt = $conn->prepare($sql1);
			$stmt->bindParam(':Advisor_ID', $Advisor_ID, PDO::PARAM_STR);
			$stmt->execute();
			
			// If Advisor exist, load info
			if($stmt->rowCount())
			{
				$row_all1 = $stmt->fetchall(PDO::FETCH_ASSOC);
					
				// Assign result variables to result array
				$result1[0]['Advisor_Name'] = $row_all1[0]['Advisor_Name'];
				$result1[0]['Advisor_Email'] = $row_all1[0]['Advisor_Email'];
				
				// Query for Aptmt_Date, Aptmt_Time
				$sql2 = 'SELECT Aptmt_Date, Aptmt_Time FROM appointment WHERE Appointment_ID = :Appointment_ID';
				$stmt = $conn->prepare($sql2);
				$stmt->bindParam(':Appointment_ID', $appointmentId, PDO::PARAM_STR);
				$stmt->execute();
				
				// If Appointment exist, load info
				if($stmt->rowCount())
				{	
					$row_all2 = $stmt->fetchall(PDO::FETCH_ASSOC);
				
					// Assign result variables to result array
					$result1[0]['Aptmt_Time'] = $row_all2[0]['Aptmt_Time'];
					$result1[0]['Aptmt_Date'] = $row_all2[0]['Aptmt_Date'];
					
					// Innitialize variables
					$Advisee_Email = $result1[0]['Advisee_Email'];
					$Advisee_Name = $result1[0]['Advisee_Name'];
					$Advisor_Name = $result1[0]['Advisor_Name'];
					$Advisor_Email = $result1[0]['Advisor_Email'];
					$Appointment_Time = $result1[0]['Aptmt_Time'];
					$Appointment_Date = $result1[0]['Aptmt_Date'];
						
					// Mail headers	
					$headers = 'From: noreply@salemstate.edu' . "\r\n";
					$headers .= 'MIME-Version: 1.0' . "\r\n";
					$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";
		
					// Mail title
					$msg = "Appointment Cancelled!";
					
					// Mail body Advisor
					$bodyAdvisor = "Dear " . $Advisor_Name . ",<br>";
					$bodyAdvisor .= "<p>Advising Appointment Cancelled!</p><br>";
               		$bodyAdvisor .= "<p><u>Appointment Details</u></p>";
                	$bodyAdvisor .= "<p>Student Name: " . $Advisee_Name . "<p>";
                	$bodyAdvisor .= "<p>Student ID: " . $id . "<p>";
                	$bodyAdvisor .= "<p>Appointment Date: " . $Appointment_Date . "<p>";
                	$bodyAdvisor .= "<p>Appointment Time: " . $Appointment_Time . "<p>";
                	$bodyAdvisor .= "<p><i>DO NOT REPLY TO THIS Email.<i><p>";
				
					// Mail body Advisee
					$bodyAdvisee = "Dear " . $Advisee_Name . ",<br>";
					$bodyAdvisee .= "<p>You Cancelled Advising Appointment</p>";
					$bodyAdvisee .= "<p>Your Student ID: " . $id . "<p>";
					$bodyAdvisee .= "<p>Advisor Name: " . $Advisor_Name . "<p><br>";
					$bodyAdvisee .= "<p><u>Appointment Details</u></p>";
					$bodyAdvisee .= "<p>Appointment Date: " . $Appointment_Date . "<p>";
					$bodyAdvisee .= "<p>Appointment Time: " . $Appointment_Time . "<p>";
					$bodyAdvisee .= "<p><i>DO NOT REPLY TO THIS Email.<i><p>";
				
					// Send email to Advisee and Advisor
					mail($Advisor_Email, $msg, $bodyAdvisor, $headers);
					mail($Advisee_Email, $msg, $bodyAdvisee, $headers);  
								
				} 
				
				// Appointment does not exist
				elseif(!$stmt->rowCount())
				{

				}
			
			}  
			
			// Advisor does not exist
			elseif(!$stmt->rowCount())
			{
		
			}
		}  
		
		// If Appointment is not cancelled	 
		elseif(!$stmt->rowCount())
		{
			$result = "false";
		}	
		
		else
		{
			$result = "error";
		}
	
		// Send reply back
		echo $result;	
	}
	 

 
?>



