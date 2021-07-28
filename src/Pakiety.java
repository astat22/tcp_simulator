import java.util.*;
import java.io.*;

public class Pakiety 
{
	public static final boolean[] dzielnikCRC = {true,false,true,false,true};
	public static int wielkoscPakietu = 64; //wielkosc pakietu nie jest stala, ale niniejsza wielkosc stanowi dla niej baze: tekst jest dzielony na 64-bitowe fragmenty
	public static final boolean[] esc = {false,false,false,true,true,false,true,true};
	public static final boolean[] soh = {false,false,false,false,false,false,false,true};
	public static final boolean[] eot = {false,false,false,false,false,true,false,false};
	public static int polecenie;
	public static String polecenieS;
	public static CSMA_CD csma;
	public static void main(String[] args)
	{
		Pakiety obiekt = new Pakiety();
		Scanner input = new Scanner(System.in);
		polecenieS = input.nextLine();
		polecenie = Integer.parseInt(polecenieS);
		switch(polecenie)	
		{
		case 1:									//RAMKOWANIE
			obiekt.zapiszDoPliku(obiekt.czytajZPliku("Z.txt"));
			break;
		case 2:									//ODRAMKOWYWANIE
			obiekt.zapisOdramkowujacy(obiekt.czytajZPliku("W.txt"));
			break;
		case 3:									//SYMULACJA DOSTÊPU DO MEDIUM
			csma = new CSMA_CD(10);
			break;
		case 4:									//CZYTAJ BITY Z KLAWIATURY
			//obiekt.zapiszDoPliku(obiekt.czytajZKlawiatury());
			polecenieS = input.nextLine();
			obiekt.zapiszDoPliku(obiekt.stringNaBity(polecenieS));
			break;								
		default:
			break;	
		}
		input.close();
	}
	public boolean[] czytajZKlawiatury()
	{
		Scanner input2 = new Scanner(System.in);
		String linia = input2.nextLine();
		input2.close();	
		//boolean[] przeczytaneBity;// = new boolean[linia.length()];
		return stringNaBity(linia);
	}
	public boolean[] stringNaBity(String tekst)
	{
		int rozmiar=tekst.length();//, dodane=0;
		while(rozmiar%8!=0)
		{
			rozmiar++;
			//dodane++;
		}
		boolean[] przeczytaneBity = new boolean[rozmiar];
		for(int i=0;i<rozmiar;i++)
		{
			if(i<tekst.length())
			{
				if(tekst.charAt(i)=='1')
				{
					przeczytaneBity[i] = true;
				}
				else
				{
					przeczytaneBity[i] = false;
				}
			}
			else
				przeczytaneBity[i] = false;
		}
		return przeczytaneBity;
	}
	public boolean[] czytajZPliku(String nazwaPliku)
	{
		boolean[] przeczytaneBity = null;
		File wejscie = new File(nazwaPliku);
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(wejscie));
			String linia = null;
			String calosc = "";

			while((linia=br.readLine())!=null)
			{
				//System.out.println(linia);
				calosc+=linia;
			}
			br.close();
			/*Konwencja: jeœli przeczytano niewystarczaj¹c¹ iloœæ bitów, ca³oœæ dope³niamy zerami z ty³u*/
			int rozmiarTablicy = calosc.length();
			//int dodaneBity = 0;
			while(rozmiarTablicy%8!=0)
			{
				rozmiarTablicy++;
				//dodaneBity++;
			}
			przeczytaneBity = new boolean[calosc.length()];
			for(int i=0;i<rozmiarTablicy;i++)
			{
				if(i<calosc.length())
				{
					if(calosc.charAt(i)=='1')
					{
						przeczytaneBity[i] = true;
					}
					else
					{
						przeczytaneBity[i] = false;
					}
				}
				else
					przeczytaneBity[i] = false;
			}
			//if(rozmiarTablicy<wielkoscPakietu)
			//	wielkoscPakietu = rozmiarTablicy;
		}
		catch(Exception e)
		{
			System.out.print(e+" B³¹d");
		}
		//wypisz(przeczytaneBity);
		return przeczytaneBity;
	}
	public void wypisz(boolean[] bity)
	{
		for(int i=0;i<bity.length;i++)
		{
			if(i%8==0)
				System.out.print(".");
			if(bity[i])
				System.out.print("1");
			else
				System.out.print("0");
		}
		System.out.print("\n");
	}
	public void zapiszDoPliku(boolean[] surowy)
	{
		if(surowy!=null)
		{
		    File wyjscie = new File("W.txt");
		    try
		    {
		    	FileWriter fw = new FileWriter(wyjscie);
		    	Vector<boolean[]> pakiety = dzielNaPakiety(surowy);
		    	for(int j=0;j<pakiety.size();j++)
		    	{
		    		System.out.println(bityNaStringa(pakiety.get(j)));
		    		fw.write(bityNaStringa(pakiety.get(j)));
		    		//wypisz(pakiety.get(j));
		    	}
		    	fw.close();
		    }
		    catch(Exception e)
		    {
		    	System.out.println(e);
		    }
		}
		else
		{
			System.out.println("B³¹d odczytu z pliku.");
		}
	}
	public String bityNaStringa(boolean[] bity)
	{
		char[] znaki = new char[bity.length];
		for(int i=0;i<bity.length;i++)
		{
			if(bity[i])
			{
				znaki[i]='1';
			}
			else
			{
				znaki[i]='0';
			}
		}
		//znaki[bity.length]='\n';
		return String.valueOf(znaki);
	}
	public Vector<boolean[]> dzielNaPakiety(boolean[] czystyTekst)			//OBS£U¯YÆ ZBYT KRÓTKIE CI¥GI
	{
		Vector<boolean[]> pakiety = new Vector<boolean[]>();
		boolean[] bufor = new boolean[wielkoscPakietu];
		boolean nieoczekiwanePrzerwanie = false;
		int malyPakiet = wielkoscPakietu;
		for(int globalny=0;globalny<czystyTekst.length;)
		{
			if(czystyTekst.length-globalny<wielkoscPakietu)
			{
				malyPakiet = czystyTekst.length-globalny;
				bufor = new boolean[malyPakiet];
			}
			for(int lokalny=0;lokalny<malyPakiet && !nieoczekiwanePrzerwanie;lokalny++)
			{
				if(globalny+lokalny==czystyTekst.length)
				{
					nieoczekiwanePrzerwanie = true;
					//System.out.println("B³¹d kodowania - niewystarczaj¹ca liczba bitów w strumieniu wejœciowym.");
					//bufor[lokalny] = false;
				}
				else
				{
					bufor[lokalny] = czystyTekst[globalny+lokalny];
				}
			}
			pakiety.add(rozpychaj(bufor));
			globalny+=wielkoscPakietu;
		}
		return pakiety;
	}
	public int liczEsc(boolean[] surowy)
	{
		int liczbaNiedozwolonych = 0;
		boolean[] biezacyZnak = new boolean[8];
		for(int i=0;i<surowy.length;i+=8)
		{
			biezacyZnak = Arrays.copyOfRange(surowy,i,i+8);
			if(sprawdzEsc(biezacyZnak))
			{
				liczbaNiedozwolonych++;
			}
		}
		return liczbaNiedozwolonych;
	}
	public boolean[] rozpychaj(boolean[] surowy)
	{
		boolean[] obrobiony = new boolean[surowy.length+8*liczEsc(surowy)+2*8+4];	//ramka ma dlugosc= dlugosc tekstu+8*liczba niedozwolonych znakow+16 (znak poczatku i konca transmisji)
		//System.out.println(liczEsc(surowy));
		boolean bajt[] = new boolean[8];
		for(int j=0;j<8;j++)
		{
			obrobiony[j] = soh[j];
			obrobiony[obrobiony.length-12+j]=eot[j];
		}
		//wypisz(obrobiony);
		int j=0;
		for(int i=0;j<surowy.length;i+=8)
		{
			bajt = Arrays.copyOfRange(surowy,j,j+8);
			//wypisz(bajt);
			if(sprawdzEsc(bajt))
			{
				obrobiony = przepisz(esc,obrobiony,i+8);
				//System.out.println(i);
				i+=8;
			}
			obrobiony = przepisz(bajt,obrobiony,i+8);
			j+=8;
			//wypisz(obrobiony);
		}
		//wypisz(obrobiony);
		obrobiony[obrobiony.length-1] = false;
		obrobiony[obrobiony.length-2] = false;
		obrobiony[obrobiony.length-3] = false;
		obrobiony[obrobiony.length-4] = false;
		boolean[] doCRC = new boolean[obrobiony.length];
		doCRC = przepisz(obrobiony,doCRC,0);
		boolean[] crc = liczCRC(doCRC,false);
		//wypisz(crc);
		obrobiony[obrobiony.length-1] = crc[3];
		obrobiony[obrobiony.length-2] = crc[2];
		obrobiony[obrobiony.length-3] = crc[1];
		obrobiony[obrobiony.length-4] = crc[0];
		//wypisz(obrobiony);
		return obrobiony;
	}
	public boolean[] liczCRC(boolean[] tekst2,boolean wypisz)
	{
		boolean[] tekst = tekst2;
		for(int i=0;i<tekst.length-4;i++)
		{
			if(tekst[i])
			{
				if(wypisz)
				{
					System.out.println(bityNaStringa(tekst));
					for(int j=0;j<i;j++)
						System.out.print(".");
					System.out.println(bityNaStringa(dzielnikCRC));
				}
				tekst[i] = tekst[i]^dzielnikCRC[0];
				tekst[i+1] = tekst[i+1]^dzielnikCRC[1];
				tekst[i+2] = tekst[i+2]^dzielnikCRC[2];
				tekst[i+3] = tekst[i+3]^dzielnikCRC[3];
				tekst[i+4] = tekst[i+4]^dzielnikCRC[4];
			}
		}
		boolean[] crc = new boolean[4];
		crc[3] = tekst[tekst.length-1];
		crc[2] = tekst[tekst.length-2];
		crc[1] = tekst[tekst.length-3];
		crc[0] = tekst[tekst.length-4];
		return crc;
	}
	public boolean[] przepisz(boolean[] zrodlo, boolean[] cel, int poczCel)
	{
		for(int i=0;i<zrodlo.length;i++)
		{
			cel[i+poczCel] = zrodlo[i];
		}
		//wypisz(cel);
		return cel;
	}
	public boolean[] przepiszR(boolean[] zrodlo, boolean[] cel, int poczCel)
	{
		for(int i=0;i<8;i++)
		{
			cel[i] = zrodlo[i+poczCel];
		}
		//wypisz(cel);
		return cel;
	}
	public boolean sprawdzEsc(boolean[] znak)
	{
		boolean czyZnakZakazany = false;
		if(Arrays.equals(znak,esc))
		{
			czyZnakZakazany = true;
		}
		else
			if(Arrays.equals(znak,soh))
			{
				czyZnakZakazany = true;
			}
			else
				if(Arrays.equals(znak,eot))
				{
					czyZnakZakazany = true;
				}
		return czyZnakZakazany;
	}
	public boolean[] czytajBajt(boolean[] zrodlo, int pocz)
	{
		if(pocz+8<=zrodlo.length)
		{
			return Arrays.copyOfRange(zrodlo,pocz,pocz+8);
		}
		else
		{
			System.out.println("B³¹d przekroczenia tablicy przy czytaniu bajtów.");
			return new boolean[1];
		}
	}
	public boolean[] czytajPoprzedniBajt(boolean[] zrodlo, int pocz)
	{
		return Arrays.copyOfRange(zrodlo,pocz-8,pocz);
	}
	public boolean sprawdzPoprawnosc(boolean[] ramka)
	{
		boolean[] sprawdzany = new boolean[ramka.length];
		sprawdzany = przepisz(ramka,sprawdzany,0);
		boolean[] crc = liczCRC(sprawdzany,true);
		if(!crc[0]&&!crc[1]&&!crc[2]&&!crc[3])
			return true;
		else
		{
			System.out.print(bityNaStringa(sprawdzany));
			return false;		
		}
	}
	public Trojka rozpoznajRamke(boolean[] ciagRamek, int pocz) 
	{
		int it = pocz;
		//System.out.print("s="+it);
		boolean przerwij = false;
		Trojka trojka = new Trojka();
		/*Wyznaczanie pocz¹tku ramki.*/
		for(;!przerwij && it<ciagRamek.length;it++)
		{
			if(it<8)
			{
				if(Arrays.equals(czytajBajt(ciagRamek,it),soh))
				{
					trojka.poczatek = it--;
					przerwij = true;
					//System.out.println(" p="+trojka.poczatek);
				}
			}
			else
			{
				if(Arrays.equals(czytajBajt(ciagRamek,it),soh) && !Arrays.equals(czytajPoprzedniBajt(ciagRamek,it),esc))
				{
					trojka.poczatek = it--;
					przerwij = true;
					//System.out.println(" p="+trojka.poczatek);
				}
			}
		}
		if(trojka.poczatek<0)
		{
			System.out.print("p="+trojka.poczatek);
			System.out.println(": Nie odczytano ¿adnej ramki.");
			return null;
		}
		/*Wyznaczanie koñca ramki*/
		przerwij = false;
		//it+=8;	//pocz¹tkowy bajt ma 8 bitów
		for(;!przerwij && it<ciagRamek.length;it+=8)	//idziemy co bajt
		{
			if(Arrays.equals(czytajBajt(ciagRamek,it),eot) && !Arrays.equals(czytajPoprzedniBajt(ciagRamek,it),esc))
			{
				trojka.koniec = it+8+4;	//+crc
				przerwij = true;
				//System.out.print("!");
			}	
			//System.out.println(bityNaStringa(czytajBajt(ciagRamek,it)));
		}
		if(trojka.koniec<1)
		{
			System.out.print("Nie odczytano ¿adnej ramki: koniec= ");
			System.out.println(trojka.koniec);
			return null;			
		}
		trojka.ramka = Arrays.copyOfRange(ciagRamek,trojka.poczatek,trojka.koniec);
		//System.out.println("Rozpoznano ramkê: "+bityNaStringa(trojka.ramka));
		return trojka;
	}
	public boolean[] odramkuj(boolean[] ramka)
	{
		if(!sprawdzPoprawnosc(ramka))
		{
			System.out.println(": Niepoprawny CRC!");
			return null;
		}
		//System.out.println("CRC poprawny."+(-4+ramka.length-8*(2+liczEsc(ramka)/2-1)));
		//System.out.println(bityNaStringa(ramka));
		boolean[] odramkowana = new boolean[ramka.length-8*(2+liczEsc(ramka)/2-1)-4];		//liczEsc policzy nam 2+2*liczbaEsc, bo soh, eot + liczbaEsc*2
		int j=8;
		boolean[] bajt = new boolean[8];
		for(int i=0;i<odramkowana.length;i+=8)
		{
			bajt = przepiszR(ramka,bajt,j);
			//System.out.println(bityNaStringa(bajt));
			/* Przeskocz znaki ucieczki */
			if(Arrays.equals(bajt,esc))
			{
				j+=8;
				bajt = przepiszR(ramka,bajt,j);			
			}
			/* Przepisz w³aœciwy bajt*/
			for(int k=i;k<i+8;k++)
			{
				odramkowana[k] = bajt[k-i];
			}
			j+=8;
		}
		return odramkowana;
	}
	/**
	 * Zadaniem tej metody jest wy³uskanie z ci¹gu bitów ramek i odczytanie pierwotnej wiadomoœci.
	 * 
	 * @param ramki
	 * @return
	 */
	public boolean[] zlozRamki(boolean[] ramki)
	{
		int biezacyBit = 0;
		Trojka biezacaTrojka;
    	Vector<boolean[]> pakiety = new Vector<boolean[]>();
    	boolean[] odramkowanyPakiet;
		while(biezacyBit<ramki.length)	//poruszaj siê po ci¹gu bitów
		{
			biezacaTrojka = rozpoznajRamke(ramki,biezacyBit);
			if(biezacaTrojka!=null)	//Znalaz³ ramkê
			{
				//System.out.println("Ramka: "+bityNaStringa(biezacaTrojka.ramka));
				odramkowanyPakiet = odramkuj(biezacaTrojka.ramka);
				pakiety.add(odramkowanyPakiet);
				//System.out.println("Odramkowano: "+bityNaStringa(odramkowanyPakiet));
				biezacyBit = biezacaTrojka.koniec;
			}
			else
			{
				System.out.println("Koniec ramek.");
				biezacyBit+=ramki.length;	//wyjscie z While
			}
		}
		return vectorNaTablice(pakiety);
	}
	public boolean[] vectorNaTablice(Vector<boolean[]> pakiety)
	{
		int dlugoscTablicy = 0;
		for(int i=0;i<pakiety.size();i++)
		{
			dlugoscTablicy+=pakiety.get(i).length;
		}
		boolean[] tablica = new boolean[dlugoscTablicy];
		int iteratorTab = 0;
		for(int j=0;j<pakiety.size();j++)
		{
			for(int k=0;k<pakiety.get(j).length;k++)
			{
				tablica[iteratorTab] = pakiety.get(j)[k];
				iteratorTab++;
			}
		}
		return tablica;
	}
	public void zapisOdramkowujacy(boolean[] odczytaneBity)
	{
		if(odczytaneBity!=null)
		{
		    File wyjscie = new File("Z2.txt");
		    try
		    {
		    	FileWriter fw = new FileWriter(wyjscie);
		    	odczytaneBity = zlozRamki(odczytaneBity);
		    	System.out.println(bityNaStringa(odczytaneBity));
		    	fw.write(bityNaStringa(odczytaneBity));
		    	fw.close();
		    }
		    catch(Exception e)
		    {
		    	System.out.println(e);
		    }
		}
		else
		{
			System.out.println("B³¹d odczytu z pliku.");
		}	
	}
	/*######################################## CSMA/CD ###########################################*/
}
/*TODO
 * konstruktor z: dlugosc kabla, przepustowosc
 *  sieci, dlugosc ramki
 * srednia liczba pakietow wysylanych na minute p6rzez uzytkownika
 */
	class CSMA_CD
	{
		boolean medium = true;				//medium==true oznacza wolne medium transmisyjne
		int iluNadaje = 0;
		int d = 1000;
		int propagacja = 500;
		Nadawca[] uzytkownicy;
		Random losowa = new Random();
		public CSMA_CD(int iluUzytkownikow, int[] sredniaLiczbaPakietow, int dlugoscKabla, int przepustowoscSieci, int dlugoscRamki)
		{
			this.uzytkownicy = new Nadawca[iluUzytkownikow];
			int nr = 0;
			for(Nadawca nad : this.uzytkownicy)
			{
				nad = new Nadawca(nr);
				nr++;
				nad.start();
			}
		}
		class Nadawca extends Thread
		{
			boolean przerwij = false;
			public boolean czeka = false;
			int identyfikator;
			public Nadawca(int nr)
			{
				this.identyfikator = nr;
				//this.start();
			}
			public void run()
			{
				int czekaj;
				while(!przerwij)
				{
					czekaj = 100*losowa.nextInt(100);
					try
					{
						sleep(czekaj);
					}
					catch(InterruptedException e)
					{
						System.out.println(e);
					}
					System.out.println(identyfikator+" chce nadawaæ; nadaje procesów: "+iluNadaje);
					if(iluNadaje==0)
					{
						nadawaj(d);
					}
					else
					{
						czekaj();
					}
				}
			}
			public void nadawaj(int d)
			{

				if(iluNadaje==0)
				{
					System.out.println(identyfikator+": rozpoczyna nadawanie");
					int  r = propagacja*(losowa.nextInt(10)+1)+1;
					int it = propagacja;
					boolean wiedza = false;
					boolean przerwij = false;
					while(r>=0 && !przerwij)
					{
						try
						{
							sleep(1);
						}
						catch(Exception e){}
						//synchronized 
						{
						if(it==0)
						{
							System.out.println("Wszyscy wiedz¹, ¿e "+identyfikator+" nadaje");
							iluNadaje++;
							wiedza = true;
							//medium = false;
						}
						if(wiedza==false && iluNadaje>0)
						{
							przerwij=true;
							System.out.println(identyfikator+" wykry³ kolizjê - nadaje "+iluNadaje+"; o nim wiadomo: "+wiedza);
							losuj(2*d,wiedza,it);
						}
						if(wiedza==true && iluNadaje>1)
						{
							przerwij= true;
							System.out.println(identyfikator+" wykry³ kolizjê - nadaje "+iluNadaje+"; o nim wiadomo: "+wiedza);
							losuj(2*d,wiedza,it);
						}
						/*if(przerwij)
						{
							System.out.println(identyfikator+" wykry³ kolizjê - nadaje "+iluNadaje+"; o nim wiadomo: "+wiedza);
							losuj(2*d,wiedza,it);
						}*/
						}
						if(it>=0) it--;
						r--;
					}
					if(!przerwij)
						konczNadawanie();
				}
				else
				{
					czekaj();
				}

			}
			public void losuj(int d, boolean wiedza, int it)
			{

				int r = losowa.nextInt(2*d)+propagacja+1;
				System.out.println(identyfikator+": wylosowa³ "+ r);
				try
				{

					int odlacz = propagacja;
						while(it>=0)
						{
							sleep(1);
							it--;
							r--;
							odlacz--;
						}
						if(!wiedza)
						{
							System.out.println("Wszyscy wiedz¹, ¿e "+identyfikator+" nadawa³");
							iluNadaje++;
						}
						while(r>0)
						{
							sleep(1);
							if(odlacz==0)
							{
								System.out.println("Wszyscy wiedz¹, ¿e "+identyfikator+" przesta³ nadawaæ");
								iluNadaje--;
							}
							r--;
							odlacz--;
						}
						if(iluNadaje>0)
						{
							czekaj();
						}
						else
						{
							System.out.println(identyfikator+" próbuje siê wstrzeliæ po losowaniu");
							nadawaj(d);
						}
					}
					
				
				catch(Exception e)
				{
					System.out.println(e);
				}

			}
			public void czekaj()
			{
				czeka = true;
				//System.out.println(identyfikator+": czeka");
				//wypiszCzekajacych();
				//while(!medium){}
				//System.out.println(identyfikator+": koniec czekania");
				//czeka = false;
				int r = 500*losowa.nextInt(100);
				try
				{
					sleep(r);
				}
				catch(InterruptedException e)
				{
					System.out.println(e);
				}
				nadawaj(d);
			}
			public void wypiszCzekajacych()
			{
				System.out.print("Oczekuj¹ce: ");
				for(Nadawca nad : uzytkownicy)
				{
					if(nad!=null)
					if(nad.czeka)
						System.out.print(nad.identyfikator+" ");
				}
				System.out.print("\n");
			}
			public void konczNadawanie()
			{
				medium = true;
				iluNadaje--;
				System.out.println(identyfikator+": koñczy nadawanie. £¹cznie nadaje "+iluNadaje);
				try
				{
					sleep(100);
					//wait();
				}
				catch(InterruptedException e)
				{
					System.out.println(e);
				}
			}
			public void zabij()
			{
				this.przerwij = true;
			}
		}
	}

class Trojka
{
	public int poczatek = -1;
	public int koniec = -1;
	public boolean[] ramka = new boolean[1];
}
