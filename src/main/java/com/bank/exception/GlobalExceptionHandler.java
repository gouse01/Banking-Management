package com.bank.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the MVC layer.
 * Renders a friendly error page instead of exposing stack traces.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        return buildErrorView(ex.getMessage(), model);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public String handleAccountNotFound(AccountNotFoundException ex, Model model) {
        return buildErrorView(ex.getMessage(), model);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalance(InsufficientBalanceException ex, Model model) {
        return buildErrorView(ex.getMessage(), model);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public String handleInvalidAmount(InvalidAmountException ex, Model model) {
        return buildErrorView(ex.getMessage(), model);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public String handleDuplicateEmail(DuplicateEmailException ex, Model model) {
        return buildErrorView(ex.getMessage(), model);
    }

    @ExceptionHandler(InvalidAccountException.class)
    public String handleInvalidAccount(InvalidAccountException ex, Model model) {
        return buildErrorView(ex.getMessage(), model);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public String handleInvalidCredentials(InvalidCredentialsException ex, Model model) {
        return buildErrorView(ex.getMessage(), model);
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        return buildErrorView("An unexpected error occurred. Please try again later.", model);
    }

    private String buildErrorView(String message, Model model) {
        model.addAttribute("errorMessage", message);
        return "error";
    }
}
