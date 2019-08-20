DROP TRIGGER if exists Before_Insert_ticket;

DELIMITER $$
CREATE TRIGGER Before_Insert_ticket
BEFORE INSERT ON `onboard`
FOR EACH ROW
BEGIN
	declare s,c int;
	SET c=(SELECT DISTINCT capacity FROM `onboard`, `flights` , `planes`
	WHERE new.flight_no= flights.flight_no
	AND flights.tail_no= planes.tail_no);
    Set s=(SELECT MAX(seat)  FROM OnBoard where NEW.flight_no=ONBoard.flight_no)+1;
	if s is null then set s=1; 
    END IF;
	IF (s>c) THEN
		SIGNAL SQLSTATE VALUE '45000' SET MESSAGE_TEXT = 'INSERT failed - The capacity of the plane is full';
	else set New.seat=s;
	END IF;
END$$
DELIMITER ;





