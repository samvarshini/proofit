# 🔐 ProofIt — Tamper-Proof Document Timestamping

> Prove you created it. Before anyone else.

ProofIt is a full-stack web application that uses **SHA-256 hash chaining** — the same data structure powering Bitcoin and Git — to create tamper-proof certificates of document originality.

## 🎯 Problem It Solves
Students submit assignments, researchers share ideas, creators publish work — but there's no way to **prove you created something first**. ProofIt solves this with cryptographic proof.

## ⚙️ How It Works
1. User uploads any document
2. App generates a **SHA-256 hash** of the file bytes
3. Hash is chained to previous document's hash (like a blockchain)
4. A **verification token** is issued — shareable with anyone
5. Anyone can verify the document's authenticity via public URL — no login needed

## 🧠 Core Algorithm — Hash Chaining
