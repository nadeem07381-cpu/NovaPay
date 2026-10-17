const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY RULES VALIDATION TESTS ---

test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      uid: BOB_UID,
      displayName: "Bob Smith",
      email: "bob@example.com",
      balance: 5000,
      accountNumber: "PK76NOVA1111",
      accountStatus: "ACTIVE",
      preferredCurrency: "PKR",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Authenticated user: can create own valid profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      uid: ALICE_UID,
      displayName: "Alice Khan",
      email: "alice@example.com",
      balance: 25000,
      accountNumber: "PK76NOVA2222",
      accountStatus: "ACTIVE",
      preferredCurrency: "PKR",
      onboardingCompleted: true,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

test("Authenticated user: cannot forge another UID in profile create", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      uid: BOB_UID, // Mismatched UID
      displayName: "Alice Khan",
      email: "alice@example.com",
      balance: 25000,
      accountNumber: "PK76NOVA2222",
      accountStatus: "ACTIVE",
      preferredCurrency: "PKR",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

test("Transactions: user can create valid transaction and query own transactions", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("transactions").doc("tx_001").set({
      userId: ALICE_UID,
      type: "SEND",
      title: "Sent to Kamran",
      description: "Lunch payment",
      amount: 1500,
      currency: "PKR",
      category: "Transfer",
      status: "COMPLETED",
      recipientName: "Kamran Ali",
      recipientAccount: "03001234567",
      referenceId: "NP-REF-889911",
      demoTransaction: true,
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );

  // Query own transactions
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("transactions").get()
  );
});

test("Transactions: transactions are immutable once created", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("transactions").doc("tx_002").set({
      userId: ALICE_UID,
      type: "SEND",
      title: "Initial",
      amount: 1000,
      currency: "PKR",
      category: "Transfer",
      status: "COMPLETED",
      referenceId: "NP-REF-1122",
      demoTransaction: true,
      createdAt: new Date()
    });
  });

  // Attempt to update transaction - must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("transactions").doc("tx_002").update({
      amount: 999999
    })
  );

  // Attempt to delete transaction - must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("transactions").doc("tx_002").delete()
  );
});

test("Cross-user isolation: Bob cannot read or create Alice's transactions", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("transactions").get()
  );
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("transactions").doc("tx_hack").set({
      userId: BOB_UID,
      type: "RECEIVE",
      title: "Fake deposit",
      amount: 50000,
      currency: "PKR",
      category: "Deposit",
      status: "COMPLETED",
      referenceId: "NP-HACK",
      demoTransaction: true,
      createdAt: new Date()
    })
  );
});
