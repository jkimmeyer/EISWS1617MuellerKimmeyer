/*
 *  EISWS1617
 * 
 *  Auqaapp - Server - Start
 *
 *  Autor: Moritz Müller
 */

module.exports = {
    init: function (app) {
		
		app.route('/')

            .get(function (req, res) {
				
				res.send("Server läuft!");
				
            });
			
	}
}