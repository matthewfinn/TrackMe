<?php
	mysqli_connect("danu6.it.nuigalway.ie","mydb2858fm","xa4zud","mydb2858");

	if(isset($_POST["username"])){
    
    $username = $_POST["username"];
	}
	if(isset($_POST["password"])){
    
    $password = $_POST["password"];
	}
	
	if (!empty($_POST)) {

		// if (empty($_POST['username'])) {
		// 	$response["success"] = 0; 
		// 	$response["message"] = "Username not entered.";
		// 	die(json_encode($response)); 
		// }

		// if (empty($_POST['password'])) {
		// 	$response["success"] = 0; 
		// 	$response["message"] = "Password not entered.";
		// 	die(json_encode($response)); 
		// }


		$loginQuery = " SELECT * FROM login WHERE username = '$username'and password='$password'";

		$sql = mysqli_query($loginQuery);

		$array = mysqli_fetch_array($sql);

		// if(!empty($array)){

		// 	$response["success"] = 1;
		// 	$response["message"] = "Login Successful";
		// 	die(json_encode($response));

		// }else{
		// 	$response["success"] = 0;
		// 	$response["message"] = "Invalid Login Details";
		// 	die(json_encode($response));
		// }

	}else{
		 $response["success"] = 0;
		 $response["message"] = "Both username & password fields are empty!";
		 die(json_encode($response));
	}

	mysqli_close();
?>