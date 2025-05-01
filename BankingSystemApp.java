import java.util.*;

public class BankingSystemApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankingService service = new BankingServiceImpl();

        while (true) {
            System.out.println("\nBanking System");
            System.out.println("1. Add Customer\n2. Add Account\n3. Add Beneficiary\n4. Add Transaction");
            System.out.println("5. Find Customer by Id\n6. List all Accounts of Customer\n7. List all Transactions of Account");
            System.out.println("8. List all Beneficiaries of Customer\n9. Exit\nEnter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Customer Id: ");
                    int cid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Address: ");
                    String address = sc.nextLine();
                    System.out.print("Contact No.: ");
                    String contact = sc.nextLine();
                    service.addCustomer(new Customer(cid, name, address, contact));
                    break;
                case 2:
                    System.out.print("Account Id: ");
                    int aid = sc.nextInt();
                    System.out.print("Customer Id: ");
                    int custId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Account Type (Saving/Current): ");
                    String type = sc.nextLine();
                    System.out.print("Balance: ");
                    double bal = sc.nextDouble();
                    service.addAccount(new Account(aid, custId, type, bal));
                    break;
                case 3:
                    System.out.print("Beneficiary Id: ");
                    int bid = sc.nextInt();
                    System.out.print("Customer Id: ");
                    int bcust = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Name: ");
                    String bname = sc.nextLine();
                    System.out.print("Account Number: ");
                    String bacc = sc.nextLine();
                    System.out.print("Bank Details: ");
                    String bdetails = sc.nextLine();
                    service.addBeneficiary(new Beneficiary(bid, bcust, bname, bacc, bdetails));
                    break;
                case 4:
                    System.out.print("Account Id: ");
                    int transAid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Type (Deposit/Withdrawal): ");
                    String transType = sc.nextLine();
                    System.out.print("Amount: ");
                    double amt = sc.nextDouble();
                    service.addTransaction(new Transaction(transAid, transType, amt));
                    break;
                case 5:
                    System.out.print("Customer Id: ");
                    int fid = sc.nextInt();
                    System.out.println(service.findCustomerById(fid));
                    break;
                case 6:
                    System.out.print("Customer Id: ");
                    int acust = sc.nextInt();
                    List<Account> accList = service.getAccountsByCustomerId(acust);
                    accList.forEach(System.out::println);
                    break;
                case 7:
                    System.out.print("Account Id: ");
                    int tacc = sc.nextInt();
                    List<Transaction> transList = service.getTransactionsByAccountId(tacc);
                    transList.forEach(System.out::println);
                    break;
                case 8:
                    System.out.print("Customer Id: ");
                    int bcustId = sc.nextInt();
                    List<Beneficiary> benList = service.getBeneficiariesByCustomerId(bcustId);
                    benList.forEach(System.out::println);
                    break;
                case 9:
                    System.out.println("Thank you!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
