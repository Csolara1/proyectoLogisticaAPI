package com.csl.app.service;

import com.csl.app.model.Quote;
import java.util.List;

public interface QuoteService {
    Quote saveQuote(Quote quote);
    Quote getQuoteById(Long id);
    List<Quote> getAllQuotes();
}