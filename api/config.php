<?php

	// Initialize variables
	$servername = " ";
	$username = " ";
	$password = " ";
	$dbname = " ";

	try {
			$conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
			$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		}
	catch(PDOException $e)
		{
			die("OOPs something went wrong");
		}

?>