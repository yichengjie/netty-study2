package com.yicj.iterator.iterator;

import com.yicj.iterator.aggregate.BookShelf;
import com.yicj.iterator.entity.Book;

public class BookShelfIterator implements com.yicj.iterator.iterator.Iterator {
    private BookShelf bookShelf ;
    private int index ;

    public BookShelfIterator(BookShelf bookShelf){
        this.bookShelf = bookShelf ;
    }
    @Override
    public boolean hasNext() {
        if(index < bookShelf.getLength()){
            return true ;
        }
        return false;
    }

    @Override
    public Object next() {
        Book book = bookShelf.getBookAt(index) ;
        this.index ++ ;
        return book;
    }
}
