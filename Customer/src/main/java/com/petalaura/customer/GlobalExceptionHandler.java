package com.petalaura.customer;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ControllerAdvice
    public class GlobalExceptionHandlerr {

        @ExceptionHandler(IllegalArgumentException.class)
        public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
            model.addAttribute("quantityError", ex.getMessage());
            return "cart"; // Return the view name for error display
        }

    }

}