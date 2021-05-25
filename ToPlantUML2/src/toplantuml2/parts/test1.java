package toplantuml2.parts;

public class test1 {
	int licznik =0;
	public int getLicznik() {
		licznik++;
		return licznik;
	}
}
class test22 extends test1{
	int licznik2 = getLicznik();
}
class Test12 extends test2{

}

class Testowa1 extends test1{
	int licznik1;
	int licznik2;
}