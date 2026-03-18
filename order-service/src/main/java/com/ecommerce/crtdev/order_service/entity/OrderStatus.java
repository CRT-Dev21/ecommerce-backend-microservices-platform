package com.ecommerce.crtdev.order_service.entity;

import java.util.Set;

public enum OrderStatus {
    PENDING {
        @Override
        public Set<OrderStatus> validTransitions (){
            return Set.of(AWAITING_PAYMENT, CANCELLED);
        }
    },
    AWAITING_PAYMENT {
        @Override
        public Set<OrderStatus> validTransitions (){
            return Set.of(PAYMENT_PROCESSING, CANCELLED);
        }
    },
    PAYMENT_PROCESSING {
        @Override
        public Set<OrderStatus> validTransitions (){
            return Set.of(CONFIRMED, CANCELLED);
        }
    },
    CONFIRMED {
        @Override
        public Set<OrderStatus> validTransitions (){
            return Set.of(REFUNDING);
        }
    },
    REFUNDING {
        @Override
        public Set<OrderStatus> validTransitions (){
            return Set.of(REFUNDED);
        }
    },
    REFUNDED {
        @Override
        public Set<OrderStatus> validTransitions (){
            return Set.of();
        }
    },
    CANCELLED {
        @Override
        public Set<OrderStatus> validTransitions (){
            return Set.of();
        }
    };


    public abstract Set<OrderStatus> validTransitions();

    public boolean canTransitionTo(OrderStatus target){
        return validTransitions().contains(target);
    }
}
