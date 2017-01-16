/*
 *  EISWS1617
 * 
 *  Auqaapp - Server
 *
 *  Autor: Moritz Müller
 */

module.exports = {
    init: function (app) {
		
		app.route('/naehrstoffverbrauch')

            /* * * * * * * * * * * * * * * * * * * * * * * * * *
             *  Berechnung des täglichen Nährstoffverbrauchs   *
			 *  mit der linearen Interpolation                 *
             * * * * * * * * * * * * * * * * * * * * * * * * * */

            .get(function (req, res) {
                
				// Nährstoffe: Phosphat (PO3), Kalium (K), Calcium (Ca), Magnesium (Mg), Eisen (FE), Mangan (Mn), 
				// Kupfer (Cu), Zink (Zi), Schwefel (S), Boron (B), Molybdän (Mo), Kohlenstoffdioxid (CO2)
				
                // Von uns berücksichtigte Nährstoffe: Kohlenstoffdioxid (CO2), Phosphat (PO3), Kalium (K), Eisen (FE)

                // In diesem Beispiel berücksichtigt: CO2 (Ist aber auch auf alle anderen anwendbar)
				
				mongoose.model('wasserwerte').find(function (err, wasserwerte) {
					
				    // Immer jeweils zwei Messungen vergleichen
				    var test = 0;
				    var points = [];
				    var c = 0;

				    for (var i = 0; i < wasserwerte.length; i++) {
				        for (var j = 0; j < wasserwerte.length; j++) {
				            var datum1 = new Date(wasserwerte[i].datum);
				            var datum2 = new Date(wasserwerte[j].datum)

                            // Wenn das erste Datum kleiner ist als das zweite Datum, kann der Verbrauch festgestellt werden

                            if (datum1 < datum2) {

                                var co2_1 = wasserwerte[i].co2;
                                var co2_2 = wasserwerte[j].co2;

                                // Der erste Zeitpunkt wird zu 0 und der zweite Zeitpunkt ist die Differenz zwischen den beiden Tagen

                                var zeitpunkt1 = 0;
                                var zeitpunkt2 = (datum2.getTime() - datum1.getTime()) / 1000 / 60 / 60 / 24;

                                // Tag 0 und den dazugehörigen CO2 Gehalt speichern und Tag x und den dazugehörigen CO2 Gehalt speichern

                                points[c++] = [{ zeitpunkt: zeitpunkt1, co2: co2_1}, { zeitpunkt: zeitpunkt2, co2: co2_2 }];

				            }
				        }
				    }

				    // Lineare Interpolation 
				    // Quelle zur Berechnung: https://www.youtube.com/watch?v=KYiIGZYrb9M

				    var message = "";
				    var verbrauch = 0;

				    for (var i = 0; i < c; i++) {

                        // Zeitpunkt und CO2 Gehalt der ersten Messung
				        var x1 = points[i][0].zeitpunkt;
				        var y1 = points[i][0].co2;

                        // Zeitpunkt und CO2 Gehalt der zweiten Messung
				        var x2 = points[i][1].zeitpunkt;
				        var y2 = points[i][1].co2;

                        // Zeitpunkt x, in diesem Fall 1, weil wir den Verbrauch pro Tag haben wollen
				        var x = 1;

                        // CO2 Gehalt an Tag 1 berechnen
				        var y = y1 + ((x - x1) / (x2 - x1)) * (y2 - y1);

                        // Ausgabe vorbereiten
				        message = message+"CO2 Verbrauch Berechnung:<br><br>" +
                                      "Tag 0: " + y1 + " mg/l<br>" +
                                      "Tag " + Math.round(x2) + ": " + y2 + " mg/l<br><br>" +
                                      "Tag " + x + ": " + Math.round(y * 100) / 100 + " mg/l<br><br><hr><br>";

                        // Alle Werte des täglichen Verbrauchs zusammenzählen und am Ende durch die Anzahl teilen
				        verbrauch += (y1-y);
				    }

				    verbrauch = verbrauch / c;

                    // Ergebnis ausgeben
				    message = message + "Der durchschnittle CO2 Verbrauch pro Tag beträgt: " + verbrauch + " mg/l";
				
					res.send(message);
				});
				
            })

	}
}