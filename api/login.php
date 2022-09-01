<?php

    include 'config.php';
	 
	// Login 
    if(isset($_POST['id']) && isset($_POST['email']))
    {
		// Innitialize Variable
		$result='';
	   	$id = $_POST['id'];
        $email = $_POST['email'];
		  
		// Query to check if Advisee exist
        $sql = 'SELECT * FROM advisee WHERE  Advisee_ID = :id AND Advisee_Email = :email';
        $stmt = $conn->prepare($sql);
        $stmt->bindParam(':id', $id, PDO::PARAM_STR);
        $stmt->bindParam(':email', $email, PDO::PARAM_STR);
        $stmt->execute();
		  
		// If Advisee exist
        if($stmt->rowCount())
        {
			$result="true";	
        }  
		  
		// If Advisee does not exist
        elseif(!$stmt->rowCount())
        {
			$result="false";
        }
		  
		// Send reply back
   		echo $result;
  	}
	
?>