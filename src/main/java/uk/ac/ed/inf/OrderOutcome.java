package uk.ac.ed.inf;

public enum OrderOutcome {
    Delivered,
    ValidButNotDelivered,
    InvalidCardNumber,
    InvalidExpiryDate,
    InvalidCvv,
    InvalidTotal,
    InvalidPizzaNotDefined,
    InvalidPizzaCount,
    InvalidPizzaCombinationMultipleSuppliers,
    Invalid
}
