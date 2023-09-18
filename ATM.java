package self.practice;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

class BankAccount {
    private String username;
    private String pin;
    private int balance;


    public BankAccount(String username, String pin) {
        this.username = username;
        this.pin = pin;
        this.balance = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount) {
        balance += amount;
        recordTransaction("Deposit", amount);
    }

    public boolean withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            recordTransaction("Withdrawal", amount);
            return true;
        }
        return false;
    }
    public void transfer(BankAccount recipient, int amount) {
        if (withdraw(amount)) {
            recipient.deposit(amount);
            recordTransaction("Transfer to " + recipient.getUsername(), amount);
        } else {
            JOptionPane.showMessageDialog(null, "Insufficient balance for transfer.");
        }
    }
    private void recordTransaction(String type, int amount) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String transactionDate = dateFormat.format(new Date());

            FileWriter fileWriter = new FileWriter(username + ".txt", true); // Append mode
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(transactionDate + " - " + type + ": " + amount);
            writer.newLine();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error recording transaction.");
        }
    }
    void PrintTransactionHistory(){
        try {
            FileReader fileReader = new FileReader(username + ".txt");
            BufferedReader reader = new BufferedReader(fileReader);

            StringBuilder transactionHistory = new StringBuilder("Transaction History:\n");
            String line;
            while ((line = reader.readLine()) != null) {
                transactionHistory.append(line).append("\n");
            }
            reader.close();

            JOptionPane.showMessageDialog(null, transactionHistory.toString(), "Transaction History", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error recording transaction.");
        }
    }
}

public class ATM {
    private static HashMap<String, BankAccount> accounts = new HashMap<>();
    private static BankAccount currentUser = null;

    public static void main(String[] args) throws IOException {
        while (true) {
            int choice = Integer.parseInt(JOptionPane.showInputDialog("1. Create Account\n2. Login\n3. Exit"));
            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    exit();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice.");
            }
        }
    }


    private static void createAccount() {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if (accounts.containsKey(username)) {
            JOptionPane.showMessageDialog(null, "Username already exists. Choose another one.");
            return;
        }

        String pin = JOptionPane.showInputDialog("Enter your PIN:");
        BankAccount account = new BankAccount(username, pin);
        accounts.put(username, account);
        try {
            FileWriter fileWriter = new FileWriter(username + ".txt");
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write("Username: " + username);
            writer.newLine();
            writer.write("Balance: 0"); // Initial balance is set to 0
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error creating account file.");
        }

        JOptionPane.showMessageDialog(null, "Account created successfully!");
    }

    private static void login() {
        String username = JOptionPane.showInputDialog("Enter your username:");
        String pin = JOptionPane.showInputDialog("Enter your PIN:");

        if (accounts.containsKey(username) && accounts.get(username).getPin().equals(pin)) {
            currentUser = accounts.get(username);
            performTransactions();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or PIN.");
        }
    }

    private static void performTransactions() {
        while (true) {
            int choice = Integer.parseInt(JOptionPane.showInputDialog(
                    "1. Deposit\n2. Withdraw\n3. Check Balance\n4. Transaction History\n5. Transfer\n6. Logout"));
            switch (choice) {
                case 1:
                    int depositAmount = Integer.parseInt(JOptionPane.showInputDialog("Enter deposit amount:"));
                    currentUser.deposit(depositAmount);
                    break;
                case 2:
                    int withdrawAmount = Integer.parseInt(JOptionPane.showInputDialog("Enter withdrawal amount:"));
                    boolean success = currentUser.withdraw(withdrawAmount);
                    if (!success) {
                        JOptionPane.showMessageDialog(null, "Insufficient balance.");
                    }
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "Current balance: " + currentUser.getBalance());
                    break;
                case 4:
                    currentUser.PrintTransactionHistory();
                    break;
                case 5:
                    transfer();
                    break;
                case 6:
                    currentUser = null;
                    return;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice.");
            }
        }
    }
    private static void transfer() {
        String recipientUsername = JOptionPane.showInputDialog("Enter recipient's username:");
        if (accounts.containsKey(recipientUsername)) {
            BankAccount recipient = accounts.get(recipientUsername);
            int amount = Integer.parseInt(JOptionPane.showInputDialog("Enter transfer amount:"));
            currentUser.transfer(recipient, amount);
        } else {
            JOptionPane.showMessageDialog(null, "Recipient not found.");
        }
    }

    private static void exit() {
        JOptionPane.showMessageDialog(null, "Thanks for using our ATM. Goodbye!");
        System.exit(0);
    }
}
