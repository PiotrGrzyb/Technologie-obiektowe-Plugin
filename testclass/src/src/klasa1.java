package src;

import java.util.List;


public class klasa1 {
int licznik;

}

class klasa2 extends klasa1 {
	klasa1 nowa = new klasa1();
	
}

class klasa3 extends klasa2{
	
}

class klasa4 extends klasa1 implements fejs{
	
}

class klasa5 extends klasa1{

}


interface fejs{
	
}

class Book {
	 
    public String title;
    public String author;
 
    Book(String title, String author)
    {
 
        this.title = title;
        this.author = author;
    }
}
class L<T>{
	
}

class Library extends L<Book> {
	 
    /*private final List<Book> books;
 
    Library(List<Book> books) { this.books = books; }
 
    public List<Book> getTotalBooksInLibrary()
    {
 
        return books;
    }*/
}

enum Minutes {
	  MINUTES
	}

