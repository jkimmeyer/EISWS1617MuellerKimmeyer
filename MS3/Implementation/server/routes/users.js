/*
 *  EISWS1617
 * 
 *  Auqaapp - Server - Users
 *
 *  Autor: Moritz Müller
 */

module.exports = {
    init: function (app) {
		
		app.route('/users')

            /* * * * * * * * * * *
             *  Liste aller User *
             * * * * * * * * * * */

            .get(function (req, res) {
                mongoose.model('users').find(function (err, users) {
                    var json = { success: "true", users: users};
                    res.json(json);
                });
            })

            /* * * * * * * * * * *
             *  User hinzufügen  *
             * * * * * * * * * * */

            .post(function (req, res) {
				
				if (!req.body.UID || !req.body.token) {
                    return res.status(400).json("Keine UID oder Token angegeben!");
                }
                else {
                    var uid = req.body.UID;
					var token = req.body.token;
                    mongoose.model('users').find({"uid": uid}, function (err, users) {
                        if (users.length > 0) {
                            // UID existiert bereits
                            res.json({success: "false"});
                        }
                        else {
							// Short ID generieren / Aktuell wird noch nicht sicher gestellt, dass die SID eindeutig ist
							var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
							
							var sid = "";
							for( var i=0; i < 4; i++ ){
								sid += possible.charAt(Math.floor(Math.random() * possible.length));
							}
							
                            // User eintragen
                            mongoose.model('users').insertMany([{ "uid": uid, "sid": sid, "token": token }], function (error, docs) {

                                if (error) {
                                    res.json(error);
                                }
                                else {
                                    res.json({ success: "true", sid: sid });
                                }

                            });
                            
                        }
                    });
                }
            })
			
			.put(function (req, res) {
				
				if (!req.body.UID || !req.body.token) {
                    return res.status(400).json("Keine UID oder Token angegeben!");
                }
                else {
                    var uid = req.body.UID;
					var token = req.body.token;
                    mongoose.model('users').findOneAndUpdate({'uid':uid}, { $set: { token: token }}, {upsert:true}, function(err, doc){
						if(err){
							return res.status(500).json({error: err});	
						}
						else{
							res.json({success: "true", sid: doc.sid});
						}
					});
                }
            });
		
		/* * * * * * * * * * * * * * * * * *
		 *  User anhand der SID ermitteln  *
		 * * * * * * * * * * * * * * * * * */
			
		app.route('/users/:sid')

            .get(function (req, res) {
				
                var sid = req.params.sid;
				
                mongoose.model('users').find({"sid": sid}, function (err, user) {
					
					if(err){
						return res.status(500).json({ success: "false" });	
					}
					else{
						var json = {success: "true", user: user};
						res.status(200).json(json);
					}
					
                });
				
            });
			
	}
}