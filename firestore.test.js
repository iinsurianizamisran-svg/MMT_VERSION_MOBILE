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

test("Unauthenticated user: cannot read patients", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("patients").get());
});

test("Authenticated user: cannot read another user's patients", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("patients").get());
});

test("Authenticated user: can create and read their own patient record", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const validPatient = {
    userId: ALICE_UID,
    name: "Ahmad bin Abdullah",
    icNumber: "880512-14-5567",
    methadoneRegNo: "MTH-2024-001",
    regimenType: "DOT",
    currentDoseMg: 60.0,
    currentDoseMl: 12.0,
    dbbDaysPerWeek: 0,
    treatmentPhase: "Penstabilan",
    therapyStartDate: "2024-01-15",
    consecutiveMissedDays: 0,
    adherenceRate: 95.0
  };

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("patients").doc("pat_1").set(validPatient)
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("patients").doc("pat_1").get()
  );
});

test("Authenticated user: invalid patient data fails validation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const invalidPatient = {
    userId: ALICE_UID,
    name: "", // empty name
    methadoneRegNo: "MTH-1",
    regimenType: "INVALID_REGIMEN",
    currentDoseMg: -10
  };

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("patients").doc("pat_bad").set(invalidPatient)
  );
});

test("Authenticated user: can create and read dispensation records", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const validDispensation = {
    userId: ALICE_UID,
    patientId: "pat_1",
    patientName: "Ahmad bin Abdullah",
    methadoneRegNo: "MTH-2024-001",
    status: "DISPENSED_DOT",
    doseMg: 60.0,
    date: "2026-10-15"
  };

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("dispensations").doc("disp_1").set(validDispensation)
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("dispensations").doc("disp_1").get()
  );
});

test("Authenticated user: can create and read appointments", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const validAppointment = {
    userId: ALICE_UID,
    patientId: "pat_1",
    patientName: "Ahmad bin Abdullah",
    methadoneRegNo: "MTH-2024-001",
    appointmentType: "DOCTOR_REVIEW",
    scheduledDate: "2026-10-22",
    status: "SCHEDULED"
  };

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("appointments").doc("app_1").set(validAppointment)
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("appointments").doc("app_1").get()
  );
});
