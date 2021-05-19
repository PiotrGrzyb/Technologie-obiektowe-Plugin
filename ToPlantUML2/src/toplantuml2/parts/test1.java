package toplantuml2.parts;

public class test1 {
	int licznik =0;
	public int getLicznik() {
		licznik++;
		return licznik;
	}
}
class test2 extends test1{
	int licznik2 = getLicznik();
}
class Test extends test2{

}