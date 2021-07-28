/**				if(medium)
				{
				int propaguj = propagacja;
				int iterator = propagacja;
				boolean zajete = true;
				boolean losuj = false;
				try
				{
					while(propaguj>0)
					{
						sleep(1);
						propaguj--;
						iterator--;
						if(iluNadaje+1>1)
						{
							if(zajete)
							{
								System.out.println(identyfikator+": wykry³ kolizjê podczas rezerwacji: nadaje "+(iluNadaje+1));
								propaguj+=propagacja;
								losuj= true;
								losuj(d,false,iteretor);
							}
							zajete=false;
							//losuj(d);
							//czekaj();
						}
						//if(iterator==1)
						//{	
						if(!losuj)	iluNadaje++;
						//}
					}
					//if(!zajete)
					//	iluNadaje--;
				}
				catch(InterruptedException e)
				{
					System.out.println(e);
				}
				if(iluNadaje>1 || !zajete)
				{
					//System.out.println(identyfikator+": wykry³ kolizjê: nadaje "+iluNadaje);
					//losuj(d,false);
				}
				else
				{
					medium = false;
					System.out.println(identyfikator+": nadaje sam; ³¹cznie nadaje osób: "+iluNadaje);
					//wypiszCzekajacych();
					int  r = 1000*losowa.nextInt(10);
					while(r>0 && zajete)
					{
						r--;
						if(iluNadaje>1)
						{
							r=-100;
							zajete = false;
							System.out.println(identyfikator+": wykry³ kolizjê podczas nadawania: nadaje "+iluNadaje);
							losuj(d,true,0);
						}
						try{ sleep(1); } catch(InterruptedException e){}
					}
					/*try
					{
						sleep(r);
					}
					catch(InterruptedException e)
					{
						System.out.println(e);
					}
					if(r>=0 && zajete)
						konczNadawanie();
				}
				}
				else
					czekaj();*/

				/*try
				{
					while(r>0 || it>0)
					{
						sleep(1);
						if(Math.abs(r-it)==1)
						{
							iluNadaje++;
						}
						r--;
						//it--;
					}
				}
				catch(InterruptedException e)
				{
					System.out.println(e);
				}
				if(iluNadaje>0)
				{
					czekaj();
				}
				else
				{
					nadawaj(2*d);
					//System.out.println(identyfikator+": trafi³ w szczelinê");
					/*iluNadaje++;
					try
					{
						sleep(30);
					}
					catch(InterruptedException e)
					{
						System.out.println(e);
					}
					if(iluNadaje>1)
					{
						System.out.println(identyfikator+": ponowna kolizja");
						losuj(2*d);
					}
					else
					{
						System.out.println(identyfikator+": brak kolizji - nadaje");
						r = 100*losowa.nextInt(100);
						try
						{
							sleep(r);
						}
						catch(InterruptedException e)
						{
							System.out.println(e);
						}
					}
					konczNadawanie();
				}*/