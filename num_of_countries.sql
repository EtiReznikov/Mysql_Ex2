 DELIMITER $$
Create Procedure num_of_countrires (IN id INT, OUT count INT)
BEGIN
SELECT count(country) From airports where country in 
(SELECT distinct country from onboard, flights where onboard.p_id= id AND onboard.flight_no=flights.flight_no and a_id=arr_loc) into count;

END$$

DELIMITER ;

DROP  Procedure num_of_countrires;