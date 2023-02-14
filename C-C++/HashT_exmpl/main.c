#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define SIZE 15

typedef struct node {
    char *word;
    struct node *next;
} HashTable_item;

typedef struct {
    int size;
    HashTable_item **table;
} HashTable;

unsigned int generate_hash(const char *word) {
    unsigned int h = 0, i;
    int word_length = strlen(word);
    for (i = 0; i < word_length; i++) {
        h = 31 * h + word[i] - 65; //suma kodow ascii liter w slowie
    }
    return h;
}


void add_word(HashTable* ht, char* word) {
    unsigned int hash = generate_hash(word) % ht->size;
    HashTable_item* new_node = malloc(sizeof(HashTable_item));
    new_node->word = (char*)malloc((strlen(word) + 1) * sizeof(char));
    strcpy(new_node->word, word);
    new_node->next = ht->table[hash];
    ht->table[hash] = new_node;
}


void display_table(HashTable *ht) {
    int i;
    for (i = 0; i < ht->size; i++) {
        HashTable_item *item = ht->table[i];
        printf("Index %d: ", i);
        while (item != NULL) {
            printf("%s ", item->word);
            item = item->next;
        }
        printf("\n");
    }
}

//HashTable_item *last_found_item = NULL;
//unsigned int last_found_hash = 0;

HashTable_item *find_word(HashTable *ht, char *word) {
    unsigned int hash = generate_hash(word) % ht->size;

//    if (last_found_item != NULL && last_found_hash == hash && strcmp(last_found_item->word, word) == 0) {
//        return last_found_item;
//    }

    HashTable_item *item = ht->table[hash];
    while (item != NULL) {
        if (strcmp(item->word, word) == 0) { //to moze isc do warunku while'a
//            last_found_item = item;
//            last_found_hash = hash;
            return item;
        }
        item = item->next;
    }
    return NULL;
}

void delete_word(HashTable *ht, char *word) {
    unsigned int hash = generate_hash(word) % ht->size;
    HashTable_item *item = ht->table[hash];
    HashTable_item *prev_item = NULL;
    while (item != NULL) {
        if (strcmp(item->word, word) == 0) {
            if (prev_item == NULL) {
                ht->table[hash] = item->next;
            } else {
                prev_item->next = item->next;
            }
            free(item->word);
            free(item);
            item->word=NULL;
            return;
        }
        prev_item = item;
        item = item->next;
    }
}

int main() {

    HashTable ht;
    ht.size = SIZE;
    ht.table = (HashTable_item **) calloc(ht.size, sizeof(HashTable_item *));

    add_word(&ht, "pies");
    add_word(&ht, "kot");
    add_word(&ht, "dom");
    add_word(&ht, "drzewo");
    add_word(&ht, "miasto");
    add_word(&ht, "abc");
    add_word(&ht, "cba");
    add_word(&ht, "acb");

    display_table(&ht);

    delete_word(&ht, "miasto");

    display_table(&ht);

    HashTable_item* item = find_word(&ht, "miasto");
    if (item != NULL) {
        printf("Found word: %s\n", item->word);
    } else {
        printf("Word not found: miasto\n");
    }

    item = find_word(&ht, "kot");
    if (item != NULL) {
        printf("Found word: %s\n", item->word);
    } else {
        printf("Word not found: kot\n");
    }

    return 0;
}
