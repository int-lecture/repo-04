<?php
	$error="";
	if($_SERVER["REQUEST_METHOD"]=="POST"){
	if(empty(htmlspecialchars(stripslashes(trim($_POST["vorname"]))))){	//Schutz gegen Javascript angriffen (empty(...))
		$error= "Bitte Vorname eingeben";
	}
	elseif(empty(htmlspecialchars(stripslashes(trim($_POST["nachname"]))))){
		$error= "Bitte Nachname eingeben";}
	elseif(empty(htmlspecialchars(stripslashes(trim($_POST["E-Mail"]))))){
		$error= "Bitte Email eingeben";}
	elseif(!filter_var($_POST["E-Mail"], FILTER_VALIDATE_EMAIL)){		//Email wird minimal auf Gültigkeit überprüft
	$error="Ungültige Email-Adresse"	;	
		}
		elseif(!isset($_POST["Datenschutz"])) {
			$error="Bitte die AGBs akzeptieren.";}
		else{
			$file = fopen("newsletter.txt", "a+");
			fwrite($file, $_POST["vorname"] . ":");
			fwrite($file, $_POST["nachname"] . ":");
		   fwrite($file, $_POST["E-Mail"] . "\n");
		   fclose($file);
		   echo "<script type='text/javascript'>alert('Erfolgreich f&uuml;r den Newsletter registriert');</script>";
		   }}
	 echo $error;
?>	