/*
 *  EISWS1617
 * 
 *  Auqaapp - Server - Wasserwerte
 *
 *  Autor: Moritz Müller
 */

module.exports = {
    init: function (app) {
		
		app.route('/wasserwerte')

            /* * * * * * * * * * * * * * * * * * * * 
             *  Wasserwerte aller Nutzer ausgeben  *
             * * * * * * * * * * * * * * * * * * * */

            .get(function (req, res) {
				
                mongoose.model('wasserwerte').find(function (err, wasserwerte) {
					var json = { success: "true", wasserwerte: wasserwerte };
					res.json(json);
				});
				
            });
			
		/* * * * * * * * * * * * * * * * * * * * * *
		 *  Wasserwerte eines bestimmten Benutzers *
		 * * * * * * * * * * * * * * * * * * * * * */
			
		app.route('/wasserwerte/:user')
		
			/* * * * * * * * * * * * * *
             *  Wasserwerte ausgeben   *
             * * * * * * * * * * * * * */
		
			.get(function (req, res) {
				
				var user = req.params.user;
				
				mongoose.model('wasserwerte').find({ "userUID": user }, function (err, wasserwerte) {
					if (wasserwerte.length > 0) {
						// Wasserwerte gefunden
						var json = { success: "true", wasserwerte: wasserwerte };
						res.json(json);
					}
					else {
						// Kein Eintrag
						res.json({ success: "true", wasserwerte: [] });
					}
				});
				
			})
			
			/* * * * * * * * * * * * * *
             *  Wasserwerte eintragen  *
             * * * * * * * * * * * * * */

            .post(function (req, res) {

                if (!req.body.von || !req.body.datum || !req.body.kh || !req.body.gh || !req.body.ph || !req.body.co2 || !req.body.eisen || !req.body.kalium || !req.body.no3 || !req.body.po3) {
                    return res.status(400).json({ success: "false", message: "Es wurden nicht alle Angaben abgeschickt!" });
                }
                else {
					
					var user = req.params.user;

                    var von = req.body.von;
                    var datum = req.body.datum;
					var kh = req.body.kh;
					var gh = req.body.gh;
					var ph = req.body.ph;
					var co2 = req.body.co2;
					var eisen = req.body.eisen;
					var kalium = req.body.kalium;
					var no3 = req.body.no3;
					var po3 = req.body.po3;

                    // Wasserwerte in DB eintragen:
					
                    var insert = [{ "userUID": user, "von": von, "datum": datum, "kh": kh, "gh": gh, "ph": ph, "co2": co2, "eisen": eisen, "kalium": kalium, "no3": no3, "po3": po3}];

                    mongoose.model('wasserwerte').insertMany(insert, function (error, docs) {

                        if (error) {
							
							// Wenn die Werte vom Benutzer eingetragen wurden, wird die Methode hier beendet.
							// Wenn sie von der Fachhandlung abgeschickt wurden, wird nur ein Log geschrieben
							// und weiter unten wird zusätzlich eine Benachrichtigung über das FCM an den
							// Nutzer geschickt
							
							if(von == "Benutzer"){
								return res.status(500).json({ success: "false", message: error });	
							}
							else{
                            	console.log(error);
							}
							
                        }
                        else {
                            if(von == "Benutzer"){
								return res.status(200).json({ success: "true" });	
							}
                        }

                    });
					
					if(von == "Fachhandlung"){

						// Notification senden
	
						var serverKey = 'AAAArt8gDDE:APA91bHbqepf4VNJKHQAUp8XY2yyTt3ntuB-s1tUII2ihwCWyhIDJmwTfali7bu8jOwAd7hMKhr7zbS9hO_VkuF9aU9Yok8Du0lb5ZgEXZdfDW_jAYDA6AM7FoeE8RgNMuNXcpxpaWXH35u3hhSiymvosvdfb2tknA';
						var fcm = new FCM(serverKey);
						
						// Token aus Datenbank lesen
						
						mongoose.model('users').find({ "uid": user }, function (err, ergebnis) {
							if (ergebnis.length > 0) {
								// User gefunden
								var token = ergebnis[0].token;
								
								var message = {
									to: token,
									notification: {
										title: 'Neue Wasserwerte',
										body: 'Deine Fachhandlung hat dir soeben neue Wasserwerte zugeschickt!'
									}
								}
			
								fcm.send(message, function (err, response) {
									if (err) {
										res.status(500).json({ success: "false", message: "Fehler beim Senden der Notification!", error: err });
									}
									else {
										res.status(200).json({ success: "true" });
									}
								});
								
							}
							else {
								// Kein User gefunden
								return res.status(500).json({ success: "false", message: "Es wurde kein Benutzer mit der UID gefunden!" });
							}
						});
						
					}

                }

            })
			
			/* * * * * * * * * * * * * *
             *  Wasserwerte löschen   *
             * * * * * * * * * * * * * */
		
			.delete(function (req, res) {
				
				if(!req.body.datum){
					return res.status(400).json({ success: "false", message: "Es wurden nicht alle Angaben abgeschickt!" });
				}
				else{
				
					var user = req.params.user;
					var datum = req.body.datum;
					
					mongoose.model('wasserwerte').find({ "userUID": user, "datum": datum }).remove(function (err, doc){
						if(err){
							return res.status(500).json({ success: "false", message: err });	
						}
						else{
							return res.status(200).json({ success: "true" });	
						}
						
					});
					
				}
				
			});
			
	}
}