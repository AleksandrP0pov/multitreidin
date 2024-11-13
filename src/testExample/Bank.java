package testExample;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Bank {

    public static void main(String[] args)  {
        System.out.println(Transfer.list);
        System.out.println("----------------------------------------------");
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 1000; i++) {
            executorService.execute(new Transfer());
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Transfer.list);
        System.out.println("ends Main");


    }
}

class Transfer implements Runnable{
      static List<BankAccount> list = createListBankAccount();

    @Override
    public void run() {
        Random random = new Random();
        int randomIndex1 = random.nextInt(list.size());
        int randomIndex2 = random.nextInt(list.size());
        int randomCash = random.nextInt(100000);

        BankAccount accountFrom = list.get(randomIndex1);
        BankAccount accountTo = list.get(randomIndex2);

        Integer toId = accountTo.getAccountNumber();
        Integer fromId = accountFrom.getAccountNumber();
        if (fromId < toId) {
            synchronized (accountFrom) {
                synchronized (accountTo) {
                    moneyTransfer(accountFrom, accountTo, randomCash);
                }
            }
        } else {
            synchronized (accountTo) {
                synchronized (accountFrom) {
                    moneyTransfer(accountFrom, accountTo, randomCash);
                }
            }
        }
    }


    public static List<BankAccount> createListBankAccount() {
        List<BankAccount> list = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            Integer id = i;
            Random random = new Random();
            int money = random.nextInt(60000) ;
            BankAccount bankAccount = new BankAccount(id, money);
            list.add(bankAccount);
        }
        return list;
    }

    public void moneyTransfer(BankAccount accountFrom, BankAccount accountTo, Integer cash) {
        System.out.println(Thread.currentThread().getName() + " Попытка перевода с " + accountFrom + " на " + accountTo + " д/с в размере " + cash);
        if (accountFrom.equals(accountTo)) {
            System.out.println("Ошибочная операция, измените номер счета");
        } else {
            if (cash > accountFrom.getCash()) {
                System.out.println("Не достаточно средств для перевода");
            } else {
                accountFrom.setCash(accountFrom.getCash() - cash);
                accountTo.setCash(accountTo.getCash() + cash);
                System.out.println(Thread.currentThread().getName() + " Транзакция произведена успешно, на счете №" + accountTo.getAccountNumber()
                        + ": " + accountTo.getCash() + " рублей");

            }
        }
    }
}

class BankAccount {
    private Integer accountNumber;
    private Integer cash;


    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getCash() {
        return cash;
    }

    public void setCash(Integer cash) {
        this.cash = cash;
    }

    public BankAccount(Integer accountNumber, Integer cash) {
        this.accountNumber = accountNumber;
        this.cash = cash;
    }

    public BankAccount() {
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber=" + accountNumber +
                ", cash=" + cash +
                '}';
    }

}




